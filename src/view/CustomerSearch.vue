<template>
  <div class="search-container card">
    <h2>🔍 손님 검색</h2>

    <div class="search-bar">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="이름 또는 전화번호 뒷자리를 입력하세요"
        @keyup.enter="searchCustomers"
      />
      <button
        @click="searchCustomers"
        :disabled="isSearching"
        class="main-search-btn"
      >
        {{ isSearching ? '중...' : '검색' }}
      </button>
    </div>

    <hr />

    <div class="result-section">
      <div v-if="customers.length > 0">
        <p class="result-count">총 {{ customers.length }}명의 검색 결과</p>

        <div class="table-responsive">
          <table class="customer-table">
            <thead>
              <tr>
                <th style="width: 10%">번호</th>
                <th style="width: 25%">이름</th>
                <th style="width: 40%">전화번호</th>
                <th style="width: 25%">관리</th>
              </tr>
            </thead>
            <tbody v-for="(customer, index) in customers" :key="customer.id">
              <tr :class="{ 'active-row': openedId === customer.id }">
                <td>{{ index + 1 }}</td>
                <td>
                  <strong>{{ customer.name }}</strong>
                </td>
                <td>{{ formatPhoneNumber(customer.phoneNumber) }}</td>
                <td>
                  <button @click="toggleDetail(customer.id)" class="detail-btn">
                    {{ openedId === customer.id ? '닫기 ▲' : '기록보기 ▼' }}
                  </button>
                </td>
              </tr>

              <tr v-if="openedId === customer.id" class="detail-drawer-row">
                <td colspan="4">
                  <div
                    class="detail-container"
                    :class="{ 'edit-mode-border': isEditMode }"
                  >
                    <div class="detail-header">
                      <span class="detail-title">
                        {{
                          isEditMode
                            ? '✏️ 세탁 기록 수정 중'
                            : '🧺 ' + customer.name + '님 최근 세탁 기록'
                        }}
                      </span>
                      <button class="close-x" @click="closeDrawer">X</button>
                    </div>

                    <div class="detail-body">
                      <div class="history-scroll-box">
                        <div v-if="isHistoryLoading" class="history-status">
                          기록을 불러오는 중...
                        </div>

                        <div
                          v-else-if="clothesHistory.length > 0"
                          class="history-table-wrapper"
                        >
                          <table class="history-table">
                            <thead>
                              <tr>
                                <th>날짜</th>
                                <th>종류</th>
                                <th>서비스</th>
                                <th>상태</th>
                                <th>회수</th>
                                <th>금액</th>
                              </tr>
                            </thead>
                            <tbody>
                              <tr v-for="item in clothesHistory" :key="item.id">
                                <td class="date-col">
                                  {{ formatDateShort(item.createdAt) }}
                                </td>

                                <template v-if="isEditMode">
                                  <td>
                                    <input
                                      v-model="item.clothesType"
                                      class="edit-input"
                                    />
                                  </td>
                                  <td>
                                    <select
                                      v-model="item.serviceType"
                                      class="edit-select service-sel"
                                    >
                                      <option value="세탁">세탁</option>
                                      <option value="드라이">드라이</option>
                                      <option value="다림질">다림질</option>
                                      <option value="수선">수선</option>
                                      <option value="기타">기타</option>
                                    </select>
                                  </td>
                                  <td>
                                    <select
                                      v-model="item.status"
                                      class="edit-select small-sel"
                                    >
                                      <option value="X">X</option>
                                      <option value="O">O</option>
                                    </select>
                                  </td>
                                  <td>
                                    <select
                                      v-model="item.pickUp"
                                      class="edit-select small-sel"
                                    >
                                      <option value="X">X</option>
                                      <option value="O">O</option>
                                    </select>
                                  </td>
                                  <td class="price-col">
                                    <div class="price-input-wrapper">
                                      <input
                                        type="number"
                                        v-model="item.price"
                                        class="edit-input-price"
                                      />
                                      <span class="unit">원</span>
                                    </div>
                                  </td>
                                </template>

                                <template v-else>
                                  <td>{{ item.clothesType }}</td>
                                  <td>{{ item.serviceType }}</td>
                                  <td>
                                    <span
                                      :class="
                                        item.status === 'O'
                                          ? 'status-badge-defect'
                                          : 'status-badge-none'
                                      "
                                    >
                                      {{ item.status }}
                                    </span>
                                  </td>
                                  <td
                                    :class="{
                                      'picked-up': item.pickUp === 'O',
                                    }"
                                  >
                                    {{ item.pickUp }}
                                  </td>
                                  <td>{{ formatPrice(item.price) }}원</td>
                                </template>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                        <div v-else class="history-status">
                          세탁 기록이 없습니다.
                        </div>
                      </div>

                      <div class="action-btns">
                        <template v-if="isEditMode">
                          <button
                            class="btn-cancel"
                            @click="isEditMode = false"
                          >
                            취소
                          </button>
                          <button
                            class="btn-save-edit"
                            @click="saveUpdatedHistory(customer.id)"
                          >
                            ✅ 변경사항 저장
                          </button>
                        </template>
                        <template v-else>
                          <button
                            class="btn-order"
                            @click="goToOrder(customer.id)"
                          >
                            🧺 새 주문 등록
                          </button>
                          <button
                            class="btn-edit-mode"
                            @click="isEditMode = true"
                          >
                            ✏️ 정보 수정
                          </button>
                        </template>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-else-if="!isSearching && hasSearched" class="no-result">
        검색 결과가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchQuery = ref('');
const customers = ref([]);
const isSearching = ref(false);
const hasSearched = ref(false);
const openedId = ref(null);
const clothesHistory = ref([]);
const isHistoryLoading = ref(false);
const isEditMode = ref(false);

const API_BASE = 'http://localhost:8080/api/store';

const searchCustomers = async () => {
  if (!searchQuery.value.trim()) {
    alert('검색어를 입력해 주세요.');
    return;
  }
  isSearching.value = true;
  hasSearched.value = true;
  openedId.value = null;
  try {
    const response = await axios.get(`${API_BASE}/search`, {
      params: { query: searchQuery.value },
    });
    customers.value = response.data;
  } catch (error) {
    alert('검색 중 오류 발생');
  } finally {
    isSearching.value = false;
  }
};

const toggleDetail = async (id) => {
  if (openedId.value === id) {
    closeDrawer();
    return;
  }
  openedId.value = id;
  isEditMode.value = false;
  isHistoryLoading.value = true;
  try {
    const response = await axios.get(`${API_BASE}/customer/${id}/clothes`);
    clothesHistory.value = response.data;
  } catch (error) {
    console.error(error);
  } finally {
    isHistoryLoading.value = false;
  }
};

const closeDrawer = () => {
  openedId.value = null;
  isEditMode.value = false;
};

const saveUpdatedHistory = async (customerId) => {
  try {
    console.log('Saving updated history:', clothesHistory.value);
    // 백엔드 파라미터 타입(List<OrderRequest>)에 맞춰 배열 전송
    await axios.put(`${API_BASE}/${customerId}/update`, {
      clothesList: clothesHistory.value,
    });
    alert('수정이 완료되었습니다.');
    isEditMode.value = false;
  } catch (error) {
    alert('저장 실패');
  }
};

const formatPhoneNumber = (num) => {
  if (!num) return '-';
  const c = ('' + num).replace(/\D/g, '');
  const m = c.match(/^(\d{3})(\d{3,4})(\d{4})$/);
  return m ? `${m[1]}-${m[2]}-${m[3]}` : num;
};

const formatDateShort = (dt) =>
  dt ? dt.substring(5, 10).replace('-', '/') : '-';
const formatPrice = (price) =>
  price ? price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') : '0';
const goToOrder = (id) =>
  router.push({ name: 'OrderCreate', query: { customerId: id } });
</script>

<style scoped>
/* 컨테이너 및 기본 테이블 */
.search-container {
  width: 95%;
  margin: 20px auto;
  background-color: #1c1616;
  color: white;
  padding: 20px;
  border-radius: 8px;
}
.search-bar input {
  flex: 1;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #4a2828;
  background-color: #2d2424;
  color: white;
  font-size: 1rem;
}
.main-search-btn {
  background-color: #42b983;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.customer-table {
  width: 100%;
  border-collapse: collapse;
}
.customer-table th {
  background-color: #2d2424;
  color: #42b983;
  padding: 15px;
}
.customer-table td {
  border-bottom: 1px solid #4a2828;
  padding: 15px;
  text-align: center;
}

/* 상세 서랍 디자인 */
.detail-container {
  background-color: #151111;
  border: 2px solid #42b983;
  margin: 10px;
  padding: 15px;
  border-radius: 10px;
  transition: border 0.3s;
}
.edit-mode-border {
  border-color: #4a90e2 !important;
}

.history-scroll-box {
  max-height: 400px;
  overflow-y: auto;
  background-color: #1a1515;
  border-radius: 8px;
  border: 1px solid #333;
}
.history-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}
.history-table th {
  position: sticky;
  top: 0;
  background-color: #2d2424;
  z-index: 5;
  border-bottom: 2px solid #42b983;
  padding: 12px;
  font-size: 0.9rem;
}
.history-table td {
  padding: 10px 5px;
  border-bottom: 1px solid #333;
  text-align: center;
  vertical-align: middle;
}

/* [핵심] 수정 모드 인풋 스타일 개선 */
.edit-input,
.edit-select {
  width: 100%;
  box-sizing: border-box;
  background: #111;
  border: 1px solid #444;
  color: white;
  padding: 8px 4px;
  border-radius: 4px;
  text-align: center;
  font-size: 0.9rem;
}

/* 상태, 회수 등 짧은 선택창 크기 최적화 */
.small-sel {
  min-width: 50px;
  max-width: 60px;
  appearance: none; /* 브라우저 기본 화살표 제거 가능 (취향껏) */
  padding-left: 10px;
}

.service-sel {
  min-width: 80px;
}

/* 금액 입력창 레이아웃 */
.price-input-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.edit-input-price {
  width: 80px;
  background: #111;
  border: 1px solid #444;
  color: white;
  padding: 8px 4px;
  border-radius: 4px;
  text-align: right;
}
.unit {
  font-size: 0.85rem;
  color: #aaa;
}

/* 상태 배지 */
.status-badge-defect {
  background: #d32f2f;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}
.status-badge-none {
  background: #444;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}
.picked-up {
  color: #42b983;
  font-weight: bold;
}

/* 버튼 디자인 */
.action-btns {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}
.btn-order,
.btn-edit-mode,
.btn-save-edit,
.btn-cancel {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
}
.btn-order {
  background-color: #42b983;
  color: white;
}
.btn-edit-mode {
  background-color: #4a4141;
  color: white;
}
.btn-save-edit {
  background-color: #4a90e2;
  color: white;
}
.btn-cancel {
  background-color: #555;
  color: white;
}

.close-x {
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
}

/* 스크롤바 커스텀 */
.history-scroll-box::-webkit-scrollbar {
  width: 6px;
}
.history-scroll-box::-webkit-scrollbar-thumb {
  background: #444;
  border-radius: 10px;
}
</style>
