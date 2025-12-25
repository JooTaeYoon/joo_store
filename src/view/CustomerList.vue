<template>
  <div class="customer-list-container card">
    <h3>📋 전체 손님 목록</h3>
    <button
      @click="fetchCustomers"
      :disabled="isLoading"
      class="refresh-button"
    >
      {{ isLoading ? '로딩 중...' : '목록 새로고침' }}
    </button>

    <div v-if="isLoading" class="state-message">
      손님 정보를 불러오는 중입니다...
    </div>
    <div v-else-if="error" class="error-state state-message">
      ⚠️ 데이터 로딩 실패: {{ error }}
    </div>

    <div v-else-if="customers.length">
      <p class="count-info">
        총 <strong>{{ customers.length }}</strong> 명
      </p>
      <table class="customer-table">
        <thead>
          <tr>
            <th>이름</th>
            <th>전화번호</th>
            <th>등록일</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody v-for="customer in customers" :key="customer.id">
          <tr :class="{ 'active-row': openedId === customer.id }">
            <td>
              <strong>{{ customer.name }}</strong>
            </td>
            <td>{{ formatPhoneNumber(customer.phoneNumber) }}</td>
            <td>{{ formatDate(customer.createdAt) }}</td>
            <td>
              <button class="toggle-btn" @click="toggleCustomer(customer.id)">
                {{ openedId === customer.id ? '닫기 ▲' : '상세보기 ▼' }}
              </button>
            </td>
          </tr>

          <tr v-if="openedId === customer.id" class="detail-row">
            <td colspan="4">
              <div class="detail-container">
                <div class="detail-header">
                  <span class="user-title"
                    >🧺 {{ customer.name }}님 상세 기록</span
                  >
                  <button class="close-x" @click="openedId = null">X</button>
                </div>
                <div class="detail-body">
                  <p>연락처: {{ formatPhoneNumber(customer.phoneNumber) }}</p>
                  <p>등록일: {{ formatDate(customer.createdAt) }}</p>
                  <div class="action-btns">
                    <button class="btn-order" @click="goToOrder(customer.id)">
                      🧺 신규 주문
                    </button>
                    <button
                      class="btn-edit"
                      @click="goToCustomerDetail(customer.id)"
                    >
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
    <div v-else class="empty-state state-message">등록된 손님이 없습니다.</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/store/get';
const router = useRouter();
const emit = defineEmits(['selectCustomer']);

const customers = ref([]);
const isLoading = ref(true);
const error = ref(null);
const openedId = ref(null);

const toggleCustomer = (id) => {
  openedId.value = openedId.value === id ? null : id;
  if (openedId.value) emit('selectCustomer', id);
};

const goToCustomerDetail = (id) => {
  router.push({ name: 'CustomerDetail', params: { id } });
};

const goToOrder = (id) => {
  alert(id + '번 손님 주문 페이지로 연결합니다.');
};

const formatPhoneNumber = (num) => {
  if (!num) return '번호 없음';
  const c = ('' + num).replace(/\D/g, '');
  const m = c.match(/^(\d{3})(\d{3,4})(\d{4})$/);
  return m ? `${m[1]}-${m[2]}-${m[3]}` : num;
};

const formatDate = (dt) => {
  if (!dt) return '-';
  return dt.substring(0, 10) + ' ' + dt.substring(11, 16);
};

const fetchCustomers = async () => {
  isLoading.value = true;
  try {
    const res = await axios.get(API_URL);
    customers.value = res.data;
  } catch (err) {
    error.value = err.message;
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchCustomers);
</script>

<style scoped>
.customer-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}
.customer-table th,
td {
  padding: 12px;
  border-bottom: 1px solid #444;
  text-align: left;
}

/* 열린 행 강조 */
.active-row {
  background-color: #333;
}

/* 상세 영역 디자인 */
.detail-row td {
  padding: 0;
  border: none;
}
.detail-container {
  background-color: #222;
  border: 2px solid #42b983;
  margin: 10px;
  padding: 15px;
  border-radius: 10px;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.user-title {
  font-size: 1.2rem;
  color: #42b983;
  font-weight: bold;
}
.action-btns {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

/* 버튼 */
button {
  cursor: pointer;
  border-radius: 5px;
  border: none;
  font-weight: bold;
}
.toggle-btn {
  background: #666;
  color: white;
  padding: 5px 10px;
}
.btn-order {
  background: #42b983;
  color: white;
  padding: 15px;
  flex: 1;
  font-size: 1.1rem;
}
.btn-edit {
  background: #4a90e2;
  color: white;
  padding: 15px;
  flex: 1;
  font-size: 1.1rem;
}
.close-x {
  background: none;
  color: white;
  font-size: 1.2rem;
}
</style>
