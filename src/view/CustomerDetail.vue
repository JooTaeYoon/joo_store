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
      <h4>👕 새 주문 접수 (총 {{ clothesList.length }} 벌)</h4>

      <form @submit.prevent="saveClothes">
        <div
          v-for="(item, index) in clothesList"
          :key="index"
          class="clothes-item-group"
        >
          <div class="clothes-item-header">
            {{ index + 1 }}번째 옷
            <button
              v-if="clothesList.length > 1"
              @click="removeClothesForm(index)"
              type="button"
              class="remove-btn"
            >
              - 제거
            </button>
          </div>

          <div class="form-group">
            <label :for="'clothesType-' + index">옷 종류:</label>
            <input
              type="text"
              :id="'clothesType-' + index"
              v-model="item.clothesType"
              required
            />
          </div>
          <div class="form-group">
            <label :for="'comment-' + index">특이사항/요청사항:</label>
            <input
              type="text"
              :id="'comment-' + index"
              v-model="item.comment"
            />
          </div>
          <hr v-if="index < clothesList.length - 1" class="item-separator" />
        </div>

        <div class="plus-btn-container">
          <button @click="addClothesForm" type="button" class="plus-btn">
            + 옷 추가
          </button>
        </div>

        <button
          type="submit"
          :disabled="isSavingClothes || clothesList.length === 0"
        >
          {{
            isSavingClothes
              ? '저장 중...'
              : '총 ' + clothesList.length + ' 벌 접수 완료'
          }}
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

// ... (props, API 주소, fetchCustomer, updateCustomer 함수는 기존과 동일)
const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
});

const API_BASE = 'http://localhost:8080/api/store';
const API_ONE = `${API_BASE}`; // 사용하지 않음
const API_UPDATE = `${API_BASE}/update`;
const API_SAVE_CLOTHES = (id) => `${API_BASE}/${id}/save/clothes`;

const isLoading = ref(true);
const customer = ref({});

const isUpdating = ref(false);
const updateMessage = ref('');
const isUpdateSuccess = ref(false);

const isSavingClothes = ref(false);
const saveMessage = ref('');
const isSaveSuccess = ref(false);

// 🛑 변경된 부분: 단일 객체 대신 배열을 사용하여 여러 벌의 옷 정보를 담습니다.
const clothesList = ref([
  {
    clothesType: '',
    comment: '',
    // 기타 필드 추가 가능
  },
]);

/**
 * 4. 새 옷 입력 폼을 clothesList에 추가합니다.
 */
const addClothesForm = () => {
  clothesList.value.push({
    clothesType: '',
    comment: '',
  });
};

/**
 * 5. 특정 옷 입력 폼을 clothesList에서 제거합니다.
 */
const removeClothesForm = (index) => {
  clothesList.value.splice(index, 1);
};

// 3. 옷 접수 및 저장 (API 호출 수정)
const saveClothes = async () => {
  isSavingClothes.value = true;
  saveMessage.value = '';
  isSaveSuccess.value = false;

  // 🛑 API 호출 시 clothesList 배열 전체를 전송합니다.
  try {
    const api = API_SAVE_CLOTHES(props.id);

    console.log('POST to API:', api);

    // clothesList 배열을 백엔드로 전송
    await axios.post(api, clothesList.value);

    isSaveSuccess.value = true;
    saveMessage.value = `총 ${clothesList.value.length} 벌 접수 완료!`;

    // 폼 초기화: 첫 번째 항목만 남기고 초기화
    clothesList.value = [
      {
        clothesType: '',
        comment: '',
      },
    ];
  } catch (error) {
    isSaveSuccess.value = false;
    saveMessage.value = '옷 접수 실패: 서버 오류가 발생했습니다.';
  } finally {
    isSavingClothes.value = false;
  }
};

// 1. 손님 한 명 정보 가져오기 (기존과 동일)
const fetchCustomer = async () => {
  isLoading.value = true;
  try {
    // API 주소 수정: /api/store/{id}/get
    const response = await axios.get(`${API_BASE}/${props.id}/get`);
    customer.value = response.data;
  } catch (error) {
    console.error('Fetch one customer error:', error);
    customer.value = {};
  } finally {
    isLoading.value = false;
  }
};

// 2. 손님 정보 수정 (기존과 동일)
const updateCustomer = async () => {
  isUpdating.value = true;
  updateMessage.value = '';
  isUpdateSuccess.value = false;

  try {
    await axios.put(`${API_UPDATE}/${props.id}`, {
      name: customer.value.name,
      phoneNumber: customer.value.phoneNumber,
    });

    isUpdateSuccess.value = true;
    updateMessage.value = '정보가 성공적으로 수정되었습니다.';
  } catch (error) {
    isUpdateSuccess.value = false;
    updateMessage.value = '수정 실패: 서버 오류가 발생했습니다.';
  } finally {
    isUpdating.value = false;
  }
};

onMounted(() => {
  fetchCustomer();
});
</script>

<style scoped>
/* 기존 스타일은 유지하고, 추가된 요소 스타일만 정의 */
.clothes-item-group {
  border: 1px solid #e0e0e0;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  background-color: #483838;
}

.clothes-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 1.1em;
  color: #fff;
}

.remove-btn {
  background-color: #ff4d4f;
  color: white;
  padding: 5px 10px;
  font-size: 0.8em;
  border-radius: 4px;
}
.remove-btn:hover {
  background-color: #cc0000;
}

.item-separator {
  margin: 15px 0;
  border-color: #ddd;
}

.plus-btn-container {
  margin: 15px 0;
  text-align: center;
}

.plus-btn {
  background-color: #42b983;
  color: white;
  padding: 8px 20px;
  border-radius: 50px;
}

/* 기존 submit 버튼과 스타일 분리 */
button[type='submit'] {
  width: 100%;
  margin-top: 20px;
  font-size: 1.1em;
}

/* 기존 스타일 */
.card {
  max-width: 600px;
  margin: 30px auto;
  padding: 25px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  background-color: #1c1616;
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
  border: 1px solid #4a2828;
  border-radius: 4px;
  box-sizing: border-box;
}
.success {
  color: #42b983;
  font-weight: bold;
}
.error {
  color: #ff4d4f;
  font-weight: bold;
}
</style>
