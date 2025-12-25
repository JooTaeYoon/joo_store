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
                  <div class="detail-container">
                    <div class="detail-header">
                      <span class="detail-title"
                        >🧺 {{ customer.name }}님 최근 세탁 기록</span
                      >
                      <button class="close-x" @click="openedId = null">
                        X
                      </button>
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
                                    'picked-up': item.pickUp === 'X',
                                  }"
                                >
                                  {{ item.pickUp }}
                                </td>
                                <td>{{ formatPrice(item.price) }}원</td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                        <div v-else class="history-status">
                          세탁 기록이 없습니다.
                        </div>
                      </div>

                      <div class="action-btns">
                        <button
                          class="btn-order"
                          @click="goToOrder(customer.id)"
                        >
                          🧺 새 주문 등록
                        </button>
                        <button class="btn-edit" @click="goToEdit(customer.id)">
                          ✏️ 정보 수정
                        </button>
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
      <div v-else-if="!hasSearched" class="initial-state">
        이름이나 번호로 손님을 찾아보세요.
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
    openedId.value = null;
    return;
  }
  openedId.value = id;
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

const formatPhoneNumber = (num) => {
  if (!num) return '번호 없음';
  const c = ('' + num).replace(/\D/g, '');
  const m = c.match(/^(\d{3})(\d{3,4})(\d{4})$/);
  return m ? `${m[1]}-${m[2]}-${m[3]}` : num;
};

const formatDateShort = (dt) => {
  if (!dt) return '-';
  return dt.substring(5, 10).replace('-', '/');
};

const formatPrice = (price) => {
  if (!price) return '0';
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

const goToOrder = (id) =>
  router.push({ name: 'OrderCreate', query: { customerId: id } });
const goToEdit = (id) =>
  router.push({ name: 'CustomerDetail', params: { id } });
</script>

<style scoped>
/* 메인 컨테이너 */
.search-container {
  max-width: 100%;
  width: 95%;
  margin: 20px auto;
  background-color: #1c1616;
  color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.5);
}
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}
.search-bar input {
  flex: 1;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #4a2828;
  background-color: #2d2424;
  color: white;
  font-size: 1.1rem;
}
.main-search-btn {
  min-width: 90px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  font-size: 1rem;
}

/* 손님 테이블 */
.table-responsive {
  width: 100%;
  overflow-x: auto;
}
.customer-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 550px;
}
.customer-table th,
.customer-table td {
  padding: 15px 10px;
  border-bottom: 1px solid #4a2828;
  text-align: center;
}
.customer-table th {
  background-color: #2d2424;
  color: #42b983;
}
.active-row {
  background-color: #2d2424;
}

/* 상세 서랍 영역 */
.detail-drawer-row td {
  padding: 0;
  border: none;
}
.detail-container {
  background-color: #151111;
  border: 2px solid #42b983;
  margin: 10px;
  padding: 20px;
  border-radius: 10px;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #444;
}
.detail-title {
  font-size: 1.2rem;
  color: #42b983;
  font-weight: bold;
}

/* [중요] 세탁 기록 스크롤 박스 */
.history-scroll-box {
  max-height: 350px;
  overflow-y: auto;
  background-color: #1a1515;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #333;
}

/* 세탁 기록 테이블 */
.history-table-wrapper {
  width: 100%;
  overflow-x: auto;
}
.history-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
  min-width: 600px;
}
.history-table th {
  background-color: #2d2424;
  color: #aaa;
  padding: 12px;
  position: sticky; /* 헤더 고정 */
  top: 0;
  z-index: 5;
  border-bottom: 2px solid #42b983;
}
.history-table td {
  padding: 12px;
  border-bottom: 1px solid #333;
  text-align: center;
}

/* 상태 배지 및 강조 */
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

/* 액션 버튼 */
.action-btns {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.btn-order,
.btn-edit {
  flex: 1;
  min-width: 150px;
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
.btn-edit {
  background-color: #4a90e2;
  color: white;
}
.close-x {
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
}

/* 커스텀 스크롤바 디자인 */
.history-scroll-box::-webkit-scrollbar {
  width: 10px;
}
.history-scroll-box::-webkit-scrollbar-track {
  background: #1c1616;
}
.history-scroll-box::-webkit-scrollbar-thumb {
  background: #444;
  border-radius: 10px;
}
.history-scroll-box::-webkit-scrollbar-thumb:hover {
  background: #42b983;
}

.history-status {
  text-align: center;
  padding: 30px;
  color: #888;
}
</style>
