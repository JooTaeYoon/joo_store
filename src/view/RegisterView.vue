<template>
  <div class="customer-registration-container">
    <h2>📝 신규 손님 등록</h2>
    <form @submit.prevent="registerCustomer">
      <div class="form-group">
        <label for="name">이름</label>
        <input type="text" id="name" v-model="customer.name" required />
      </div>

      <div class="form-group">
        <label for="phone">전화번호</label>
        <input
          type="tel"
          id="phone"
          v-model="customer.phoneNumber"
          required
          placeholder="010-0000-0000"
        />
      </div>

      <button type="submit" :disabled="isLoading">
        {{ isLoading ? '등록 중...' : '손님 등록 완료' }}
      </button>
    </form>

    <p v-if="message" :class="{ success: isSuccess, error: !isSuccess }">
      {{ message }}
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios'; // axios 설치가 필요합니다.

// API 주소
const API_URL = 'http://localhost:8080/api/store/create';

// 상태 관리
const customer = ref({
  name: '',
  phoneNumber: '',
});
const message = ref('');
const isSuccess = ref(false);
const isLoading = ref(false);

/**
 * 손님 정보를 백엔드 API로 전송하는 함수
 */
const registerCustomer = async () => {
  // 메시지 초기화
  message.value = '';
  isSuccess.value = false;
  isLoading.value = true;

  try {
    // API 호출 (백엔드에서 Customer 객체를 JSON으로 받도록 설정되어 있다고 가정)
    const response = await axios.post(API_URL, customer.value);
    console.log('response.data: ', response.data);

    // 성공 처리
    isSuccess.value = true;
    message.value = `${
      response.data.name || customer.value.name
    } 님 등록 성공! (ID: ${response.data.id})`;

    // 폼 초기화
    customer.value.name = '';
    customer.value.phoneNumber = '';
  } catch (error) {
    // 에러 처리
    isSuccess.value = false;
    let errorMessage = '손님 등록에 실패했습니다.';

    if (error.response && error.response.data && error.response.data.message) {
      // 백엔드에서 에러 메시지를 제공했을 경우
      errorMessage += ` 상세: ${error.response.data.message}`;
    } else {
      errorMessage += ` 상세: 서버 연결 오류 또는 기타 문제.`;
    }

    message.value = errorMessage;
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.customer-registration-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box; /* 패딩이 너비에 포함되도록 설정 */
}
button {
  width: 100%;
  padding: 10px;
  background-color: #42b983; /* Vue Green */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}
button:hover:not(:disabled) {
  background-color: #368261;
}
button:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}
.success {
  color: #42b983;
  margin-top: 15px;
  font-weight: bold;
}
.error {
  color: #ff4d4f;
  margin-top: 15px;
  font-weight: bold;
}
</style>
