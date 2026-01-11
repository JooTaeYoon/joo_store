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
                                <td>{{ formatDateShort(item.createdAt) }}</td>

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
                                      class="edit-select"
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
                                      class="edit-select"
                                    >
                                      <option value="X">X</option>
                                      <option value="O">O</option>
                                    </select>
                                  </td>
                                  <td>
                                    <select
                                      v-model="item.pickUp"
                                      class="edit-select"
                                    >
                                      <option value="X">X</option>
                                      <option value="O">O</option>
                                    </select>
                                  </td>
                                  <td>
                                    <input
                                      type="number"
                                      v-model="item.price"
                                      class="edit-input-price"
                                    />원
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
const isEditMode = ref(false); // 수정 모드 상태

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
    alert('검색 오류');
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

/**
 * 세탁 기록 수정 사항 저장
 * @param customerId
 */
const saveUpdatedHistory = async (customerId) => {
  console.log(clothesHistory.value);
  try {
    await axios.put(`${API_BASE}/${customerId}/update`, clothesHistory.value);
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
/* 기존 배경 및 컨테이너 스타일 그대로 유지 */
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
}
.main-search-btn {
  background-color: #42b983;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
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

/* 상세 서랍 영역 디자인 유지 */
.detail-container {
  background-color: #151111;
  border: 2px solid #42b983;
  margin: 10px;
  padding: 20px;
  border-radius: 10px;
}
.edit-mode-border {
  border-color: #4a90e2 !important;
} /* 수정 시 파란색 강조 */

.history-scroll-box {
  max-height: 350px;
  overflow-y: auto;
  background-color: #1a1515;
  border-radius: 8px;
  border: 1px solid #333;
  margin-bottom: 20px;
}
.history-table {
  width: 100%;
  border-collapse: collapse;
}
.history-table th {
  position: sticky;
  top: 0;
  background-color: #2d2424;
  z-index: 5;
  border-bottom: 2px solid #42b983;
  padding: 12px;
}
.history-table td {
  padding: 12px;
  border-bottom: 1px solid #333;
  text-align: center;
}

/* 수정 모드 입력 필드 스타일 */
.edit-input,
.edit-select,
.edit-input-price {
  background: #111;
  border: 1px solid #444;
  color: white;
  padding: 5px;
  border-radius: 4px;
  text-align: center;
  width: 80%;
}
.edit-input-price {
  width: 60px;
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

/* 하단 버튼 디자인 유지 및 추가 */
.action-btns {
  display: flex;
  gap: 12px;
}
.btn-order,
.btn-edit-mode,
.btn-save-edit,
.btn-cancel {
  flex: 1;
  padding: 16px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1.1rem;
  cursor: pointer;
}
.btn-order {
  background-color: #42b983;
  color: white;
}
.btn-edit-mode {
  background-color: #4a4141;
  color: white;
} /* 기존 '정보 수정' 버튼 색상 */
.btn-save-edit {
  background-color: #4a90e2;
  color: white;
} /* '저장' 버튼 파란색 */
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
</style>
