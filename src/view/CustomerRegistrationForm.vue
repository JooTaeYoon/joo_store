<template>
  <div class="registration-form-container card">
    <h3>📝 신규 손님 등록</h3>
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
          placeholder="010-XXXX-XXXX"
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
import axios from 'axios';

const API_URL = '/api/store/create';

const customer = ref({
  name: '',
  phoneNumber: '',
});
const message = ref('');
const isSuccess = ref(false);
const isLoading = ref(false);

const registerCustomer = async () => {
  message.value = '';
  isSuccess.value = false;
  isLoading.value = true;

  try {
    const response = await axios.post(API_URL, customer.value);

    console.log('Customer registered:', response.data);

    isSuccess.value = true;
    message.value = `${
      response.data.name || customer.value.name
    } 님 등록 성공! (ID: ${response.data.id})`;

    // 폼 초기화
    customer.value.name = '';
    customer.value.phoneNumber = '';
  } catch (error) {
    isSuccess.value = false;
    const errorMsg =
      error.response?.data?.message || '서버 연결 또는 기타 문제로 등록 실패.';
    message.value = `손님 등록 실패: ${errorMsg}`;
  } finally {
    isLoading.value = false;
  }
};
</script>
