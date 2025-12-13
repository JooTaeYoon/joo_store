<template>
  <div class="customer-detail-container card" v-if="!isLoading && customer.id">
    <h3>👤 손님 상세 정보 및 주문</h3>

    <div class="info-section">
      <h4>기본 정보 (ID: {{ customer.id }})</h4>
      <form @submit.prevent="updateCustomer">
        <div class="form-group">
          <label>이름:</label>
          <input type="text" v-model="customer.name" required />
        </div>
        <div class="form-group">
          <label>전화번호:</label>
          <input type="tel" v-model="customer.phoneNumber" required />
        </div>
        <button type="submit" :disabled="isUpdating">
          {{ isUpdating ? '수정 중...' : '손님 정보 수정' }}
        </button>
      </form>
      <p
        v-if="updateMessage"
        :class="{ success: isUpdateSuccess, error: !isUpdateSuccess }"
      >
        {{ updateMessage }}
      </p>
    </div>

    <hr />

    <div class="clothes-section">
      <h4>👕 새 주문 접수</h4>
      <form @submit.prevent="saveClothes">
        <div class="form-group">
          <label for="clothesType">옷 종류 (예: 셔츠, 코트):</label>
          <input
            type="text"
            id="clothesType"
            v-model="newClothes.clothesType"
            required
          />
        </div>
        <div class="form-group">
          <label for="comment">특이사항/요청사항:</label>
          <input type="text" id="comment" v-model="newClothes.comment" />
        </div>
        <button type="submit" :disabled="isSavingClothes">
          {{ isSavingClothes ? '저장 중...' : '옷 접수 완료' }}
        </button>
      </form>
      <p
        v-if="saveMessage"
        :class="{ success: isSaveSuccess, error: !isSaveSuccess }"
      >
        {{ saveMessage }}
      </p>
    </div>
  </div>

  <div v-else-if="isLoading" class="card state-message">
    손님 정보 로딩 중...
  </div>
  <div v-else class="card state-message error-state">
    손님 정보를 찾을 수 없습니다.
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// props로 부모 컴포넌트로부터 Customer ID를 받아옵니다.
const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
});

// API 주소 정의
const API_BASE = '/api/store';
const API_ONE = `${API_BASE}/customer-one`;
const API_UPDATE = `${API_BASE}/update`;
const API_SAVE_CLOTHES = (id) => `${API_BASE}/${id}/save/clothes`;

// 상태 관리
const isLoading = ref(true);
const customer = ref({});

// 수정 상태
const isUpdating = ref(false);
const updateMessage = ref('');
const isUpdateSuccess = ref(false);

// 옷 접수 상태
const isSavingClothes = ref(false);
const saveMessage = ref('');
const isSaveSuccess = ref(false);
const newClothes = ref({
  clothesType: '',
  comment: '',
  // category, serviceType 등은 백엔드 DTO에 맞게 추가 필요
});

// 1. 손님 한 명 정보 가져오기
const fetchCustomer = async () => {
  isLoading.value = true;
  try {
    // API: /api/store/customer-one/{id} 가정
    const response = await axios.get(`${API_ONE}/${props.id}`);
    customer.value = response.data;
  } catch (error) {
    console.error('Fetch one customer error:', error);
    customer.value = {};
  } finally {
    isLoading.value = false;
  }
};

// 2. 손님 정보 수정
const updateCustomer = async () => {
  isUpdating.value = true;
  updateMessage.value = '';
  isUpdateSuccess.value = false;

  try {
    // API: /api/store/update/{id}
    await axios.put(`${API_UPDATE}/${props.id}`, {
      name: customer.value.name,
      phoneNumber: customer.value.phoneNumber,
      // 백엔드 DTO에 필요한 필드 추가
    });

    console.log('Customer updated:', customer.value);

    isUpdateSuccess.value = true;
    updateMessage.value = '정보가 성공적으로 수정되었습니다.';
  } catch (error) {
    isUpdateSuccess.value = false;
    updateMessage.value = '수정 실패: 서버 오류가 발생했습니다.';
  } finally {
    isUpdating.value = false;
  }
};

// 3. 옷 접수 및 저장
const saveClothes = async () => {
  isSavingClothes.value = true;
  saveMessage.value = '';
  isSaveSuccess.value = false;

  try {
    // API: /api/store/{id}/save/clothes
    const api = API_SAVE_CLOTHES(props.id);

    // 백엔드는 옷 리스트를 받으므로, 현재 옷 객체를 리스트에 담아 보냅니다.
    await axios.post(api, newClothes.value);

    isSaveSuccess.value = true;
    saveMessage.value = `${newClothes.value.clothesType} 접수 완료!`;

    // 폼 초기화
    newClothes.value.clothesType = '';
    newClothes.value.comment = '';
  } catch (error) {
    isSaveSuccess.value = false;
    saveMessage.value = '옷 접수 실패: 서버 오류가 발생했습니다.';
  } finally {
    isSavingClothes.value = false;
  }
};

onMounted(() => {
  fetchCustomer();
});
</script>
