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
      <p class="count-info">총 **{{ customers.length }}** 명</p>
      <table class="customer-table">
        <thead>
          <tr>
            <th>이름</th>
            <th>전화번호</th>
            <th>등록일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customer in customers" :key="customer.id">
            <td>{{ customer.name }}</td>
            <td>{{ formatPhoneNumber(customer.phoneNumber) }}</td>
            <td>{{ formatDate(customer.createdAt) }}</td>
            <td>
              <button
                @click="
                  $emit('selectCustomer', customer.id);
                  goToCustomerDetail(customer.id);
                "
              >
                상세/수정
              </button>
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

// API 주소
const API_URL = 'http://localhost:8080/api/store/get';

// 이 컴포넌트가 부모에게 이벤트를 전달할 수 있도록 정의 (예: 특정 손님 ID를 선택했을 때)
const emit = defineEmits(['selectCustomer']);

const router = useRouter();

const goToCustomerDetail = (id) => {
  router.push({ name: 'CustomerDetail', params: { id } });
};

// 상태 관리
const customers = ref([]);
const isLoading = ref(true);
const error = ref(null);

// ... (formatPhoneNumber, formatDate 함수는 이전과 동일)
const formatPhoneNumber = (number) => {
  if (!number) return '';
  const cleaned = ('' + number).replace(/\D/g, '');
  const match = cleaned.match(/^(\d{3})(\d{3,4})(\d{4})$/);
  if (match) {
    return `${match[1]}-${match[2]}-${match[3]}`;
  }
  return number;
};

const formatDate = (datetime) => {
  if (!datetime) return '-';
  try {
    const datePart = datetime.substring(0, 10);
    const timePart = datetime.substring(11, 16);
    return `${datePart} ${timePart}`;
  } catch (e) {
    return datetime;
  }
};

/**
 * 손님 리스트를 가져오는 함수
 */
const fetchCustomers = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    const response = await axios.get(API_URL);
    customers.value = response.data;
    console.log('Fetched customers:', customers.value);
  } catch (err) {
    console.error('Fetch Error:', err);
    error.value = err.message || '알 수 없는 오류';
  } finally {
    isLoading.value = false;
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchCustomers();
});
</script>
