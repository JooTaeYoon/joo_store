<template>
  <div class="clothes-history-container card">
    <h4>🧺 손님 세탁물 이력 조회</h4>

    <div class="search-bar">
      <input
        type="number"
        v-model="searchId"
        placeholder="조회할 손님 ID 입력"
        required
      />
      <button @click="fetchClothesHistory" :disabled="isSearching">
        <span v-if="isSearching">조회 중...</span>
        <span v-else>이력 조회</span>
      </button>
    </div>

    <div v-if="isSearching && !clothes.length" class="state-message">
      세탁물 이력을 불러오는 중...
    </div>
    <div v-else-if="searchError" class="error-state state-message">
      ⚠️ 조회 실패: {{ searchError }}
    </div>

    <div v-else-if="clothes.length">
      <p class="count-info">
        총 **{{ clothes.length }}** 벌의 세탁물 기록이 있습니다.
      </p>
      <table class="clothes-table">
        <thead>
          <tr>
            <th>옷 ID</th>
            <th>종류</th>
            <th>상태</th>
            <th>서비스</th>
            <th>등록일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in clothes" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.clothesType }} ({{ item.category }})</td>
            <td>
              <span :class="getStatusClass(item.status)">{{
                item.status
              }}</span>
            </td>
            <td>{{ item.serviceType }}</td>
            <td>{{ formatDate(item.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      v-else-if="!isSearching && searchId && !searchError"
      class="empty-state state-message"
    >
      이 손님의 세탁물 이력이 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';

// API 주소 (두 API 중 하나를 사용)
// 사용자 요청 API: /api/store/customer/{id}/clothes
const API_URL = (id) => `/api/store/customer/${id}/clothes`;

// Props로 부모 컴포넌트에서 customerId를 받을 수 있게 설정 (CustomerDetail에 통합 시)
const props = defineProps({
  customerId: {
    type: [String, Number],
    default: null,
  },
});

const searchId = ref(props.customerId || null);
const clothes = ref([]);
const isSearching = ref(false);
const searchError = ref(null);

/**
 * 옷 이력을 조회하는 메인 함수
 */
const fetchClothesHistory = async () => {
  if (!searchId.value) {
    searchError.value = '손님 ID를 입력해주세요.';
    return;
  }

  isSearching.value = true;
  searchError.value = null;
  clothes.value = [];

  try {
    const response = await axios.get(API_URL(searchId.value));
    console.log('Fetching clothes history for customer ID:', searchId.value);

    // 응답 데이터가 Clothes 엔티티의 배열이라고 가정
    clothes.value = response.data;
  } catch (err) {
    console.error('Clothes History Fetch Error:', err);
    searchError.value = err.message || '세탁물 이력 조회에 실패했습니다.';
  } finally {
    isSearching.value = false;
  }
};

// props.customerId가 변경되면 자동으로 조회 ID를 업데이트하고 다시 조회
watch(
  () => props.customerId,
  (newId) => {
    if (newId) {
      searchId.value = newId;
      fetchClothesHistory();
    }
  },
  { immediate: true }
);

// 유틸리티 함수 (이전 컴포넌트와 동일)
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

// 상태별 CSS 클래스 반환 함수 (백엔드 Clothes.java의 STATUS enum 값과 매칭 필요)
const getStatusClass = (status) => {
  switch (status) {
    case 'ALL_PICKED':
      return 'status-picked';
    case 'PARTIAL':
      return 'status-partial';
    case 'NONE':
      return 'status-none';
    default:
      return '';
  }
};

// 컴포넌트가 마운트될 때 (props로 ID를 받은 경우)
if (props.customerId) {
  fetchClothesHistory();
}
</script>

<style scoped>
.clothes-history-container {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-top: 20px;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.search-bar input {
  flex-grow: 1;
}
.clothes-table {
  width: 100%;
  border-collapse: collapse;
}
.clothes-table th,
.clothes-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}
.clothes-table th {
  background-color: #f4f4f4;
}
.status-picked {
  color: green;
  font-weight: bold;
}
.status-partial {
  color: orange;
  font-weight: bold;
}
.status-none {
  color: red;
  font-weight: bold;
}
/* 기타 스타일은 CustomerList.vue와 유사하게 적용 */
.state-message {
  padding: 15px;
  text-align: center;
  border: 1px dashed #ccc;
  margin-top: 15px;
}
.error-state {
  color: #ff4d4f;
  border-color: #ff4d4f;
}
</style>
