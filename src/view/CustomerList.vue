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
              <div
                class="detail-container"
                :class="{
                  'edit-mode-border': isEditMode,
                  'order-mode-border': isOrderMode || isHistoryMode,
                }"
              >
                <div class="detail-header">
                  <span class="user-title">
                    {{
                      isEditMode
                        ? '✏️ 고객 정보 수정'
                        : isOrderMode
                        ? '🧺 새 주문 작성 (' + orderList.length + '건)'
                        : isHistoryMode
                        ? '🔍 ' + customer.name + '님 주문 내역'
                        : '👤 ' + customer.name + '님 상세 정보'
                    }}
                  </span>
                  <button class="close-x" @click="closeDrawer">X</button>
                </div>

                <div class="detail-body">
                  <div v-if="isEditMode" class="edit-view">
                    <div class="input-group">
                      <label>고객명</label>
                      <input v-model="editData.name" placeholder="이름 입력" />
                    </div>
                    <div class="input-group">
                      <label>전화번호</label>
                      <input
                        v-model="editData.phoneNumber"
                        placeholder="번호만 입력"
                      />
                    </div>
                    <div class="action-btns mt-20">
                      <button class="btn-cancel" @click="isEditMode = false">
                        취소
                      </button>
                      <button
                        class="btn-save-edit"
                        @click="updateCustomerInfo(customer.id)"
                      >
                        저장하기
                      </button>
                    </div>
                  </div>

                  <div
                    v-else-if="!isOrderMode && !isHistoryMode"
                    class="info-view"
                  >
                    <div class="info-text">
                      <p>
                        <strong>연락처:</strong>
                        {{ formatPhoneNumber(customer.phoneNumber) }}
                      </p>
                      <p>
                        <strong>등록일:</strong>
                        {{ formatDate(customer.createdAt) }}
                      </p>
                    </div>
                    <div class="action-btns">
                      <button
                        class="btn-history"
                        @click="fetchOrderHistory(customer.id)"
                      >
                        🔍 주문 내역 보기
                      </button>
                      <button class="btn-order" @click="startNewOrder">
                        🧺 신규 주문하기
                      </button>
                      <button class="btn-edit" @click="startEditMode(customer)">
                        ✏️ 정보 수정
                      </button>
                    </div>
                  </div>

                  <div v-else-if="isHistoryMode" class="history-view">
                    <div v-if="historyLoading" class="state-message">
                      내역 로딩 중...
                    </div>
                    <div
                      v-else-if="historyList.length === 0"
                      class="empty-state"
                    >
                      기존 주문 내역이 없습니다.
                    </div>
                    <div v-else class="history-table-wrapper">
                      <table class="history-table">
                        <thead>
                          <tr>
                            <th>날짜</th>
                            <th>종류</th>
                            <th>서비스</th>
                            <th>가격</th>
                            <th>메모</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="h in historyList" :key="h.id">
                            <td>{{ h.createdAt }}</td>
                            <td>{{ h.clothesType }}</td>
                            <td>{{ h.serviceType }}</td>
                            <td>{{ h.price?.toLocaleString() }}원</td>
                            <td class="memo-td">{{ h.comment || '-' }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <div class="action-btns mt-10">
                      <button class="btn-cancel" @click="isHistoryMode = false">
                        뒤로가기
                      </button>
                    </div>
                  </div>

                  <div v-else class="order-form-view">
                    <div
                      v-for="(item, index) in orderList"
                      :key="index"
                      class="order-item-card"
                    >
                      <div class="item-header">
                        <span>#{{ index + 1 }}번 품목</span>
                        <button
                          v-if="orderList.length > 1"
                          class="btn-remove"
                          @click="removeItem(index)"
                        >
                          삭제
                        </button>
                      </div>
                      <div class="order-grid">
                        <div class="input-group">
                          <label>세탁물 종류</label>
                          <input
                            v-model="item.clothesType"
                            placeholder="예: 청바지, 패딩"
                          />
                        </div>
                        <div class="input-group">
                          <label>서비스 선택</label>
                          <select v-model="item.serviceType">
                            <option value="WASH">세탁</option>
                            <option value="DRY_CLEAN">드라이</option>
                            <option value="IRON">다림질</option>
                            <option value="ALTERATION">수선</option>
                            <option value="OTHER">기타</option>
                          </select>
                        </div>
                        <div class="input-group">
                          <label>금액</label>
                          <input
                            type="number"
                            v-model="item.price"
                            placeholder="0"
                          />
                        </div>
                        <div class="input-group full">
                          <label>특이사항 (메모)</label>
                          <textarea
                            v-model="item.comment"
                            placeholder="오염 부위 등 주의사항"
                          ></textarea>
                        </div>
                      </div>
                    </div>
                    <button class="btn-add-item" @click="addNewItem">
                      ➕ 다른 품목 추가하기
                    </button>
                    <div class="total-summary">
                      총 수량: <strong>{{ orderList.length }}</strong
                      >벌 | 총 금액:
                      <strong>{{ totalPrice.toLocaleString() }}</strong
                      >원
                    </div>
                    <div class="action-btns">
                      <button class="btn-cancel" @click="isOrderMode = false">
                        취소
                      </button>
                      <button
                        class="btn-save"
                        @click="submitOrders(customer.id)"
                      >
                        ✅ 주문 {{ orderList.length }}건 저장
                      </button>
                    </div>
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
import { ref, reactive, computed, onMounted } from 'vue';
import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/store';

const customers = ref([]);
const isLoading = ref(true);
const error = ref(null);
const openedId = ref(null);

// 상태 관리용 변수들
const isOrderMode = ref(false);
const isHistoryMode = ref(false);
const isEditMode = ref(false); // 수정 모드 상태 추가

// 수정 데이터 임시 저장
const editData = reactive({
  name: '',
  phoneNumber: '',
});

// 주문 관련 변수들
const orderList = ref([]);
const historyList = ref([]);
const historyLoading = ref(false);

const totalPrice = computed(() => {
  return orderList.value.reduce(
    (sum, item) => sum + (Number(item.price) || 0),
    0
  );
});

const toggleCustomer = (id) => {
  if (openedId.value === id) {
    closeDrawer();
  } else {
    openedId.value = id;
    isOrderMode.value = false;
    isHistoryMode.value = false;
    isEditMode.value = false;
  }
};

const closeDrawer = () => {
  openedId.value = null;
  isOrderMode.value = false;
  isHistoryMode.value = false;
  isEditMode.value = false;
};

// [수정 모드 시작]
const startEditMode = (customer) => {
  editData.name = customer.name;
  editData.phoneNumber = customer.phoneNumber;
  isEditMode.value = true;
};

// [수정 정보 서버 저장]
const updateCustomerInfo = async (customerId) => {
  if (!editData.name.trim()) return alert('이름을 입력해주세요.');

  try {
    await axios.put(`${API_BASE}/update/${customerId}`, {
      name: editData.name,
      phoneNumber: editData.phoneNumber,
    });
    alert('고객 정보가 수정되었습니다.');
    isEditMode.value = false;
    fetchCustomers(); // 목록 새로고침
  } catch (err) {
    alert('수정 실패: ' + err.message);
  }
};

// --- 기존 함수들 (동일) ---
const fetchOrderHistory = async (customerId) => {
  isHistoryMode.value = true;
  historyLoading.value = true;
  try {
    const res = await axios.get(`${API_BASE}/customer/${customerId}/clothes`);
    historyList.value = res.data;
  } catch (err) {
    alert('내역 로딩 실패');
    isHistoryMode.value = false;
  } finally {
    historyLoading.value = false;
  }
};

const startNewOrder = () => {
  isOrderMode.value = true;
  isHistoryMode.value = false;
  isEditMode.value = false;
  orderList.value = [
    { clothesType: '', serviceType: 'WASH', price: 0, comment: '' },
  ];
};

const addNewItem = () =>
  orderList.value.push({
    clothesType: '',
    serviceType: 'WASH',
    price: 0,
    comment: '',
  });
const removeItem = (index) => orderList.value.splice(index, 1);

const submitOrders = async (customerId) => {
  try {
    await axios.post(`${API_BASE}/${customerId}/save/clothes`, {
      customerId,
      clothesList: orderList.value,
    });
    alert('주문 완료');
    closeDrawer();
    fetchCustomers();
  } catch (err) {
    alert('저장 실패');
  }
};

const formatPhoneNumber = (num) => {
  if (!num) return '번호 없음';
  const c = ('' + num).replace(/\D/g, '');
  const m = c.match(/^(\d{3})(\d{3,4})(\d{4})$/);
  return m ? `${m[1]}-${m[2]}-${m[3]}` : num;
};

const formatDate = (dt) =>
  dt ? dt.substring(0, 10) + ' ' + dt.substring(11, 16) : '-';

const fetchCustomers = async () => {
  isLoading.value = true;
  try {
    const res = await axios.get(`${API_BASE}/get`);
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
/* 기존 스타일 유지 및 추가 */
.customer-list-container {
  padding: 20px;
  background-color: #1c1616;
  color: white;
}
.customer-table {
  width: 100%;
  border-collapse: collapse;
}
.customer-table th,
td {
  padding: 12px;
  border-bottom: 1px solid #444;
}

.detail-container {
  background-color: #222;
  border: 2px solid #42b983;
  margin: 10px;
  padding: 15px;
  border-radius: 10px;
}
.edit-mode-border {
  border-color: #3498db !important;
} /* 수정 모드일 때 파란색 테두리 */
.order-mode-border {
  border-color: #e67e22;
}

/* 수정 폼 스타일 */
.edit-view .input-group {
  margin-bottom: 15px;
}
.edit-view label {
  display: block;
  margin-bottom: 5px;
  color: #999;
  font-size: 0.9rem;
}
.edit-view input {
  width: 100%;
  padding: 10px;
  background: #111;
  border: 1px solid #444;
  color: white;
  border-radius: 5px;
}

.btn-save-edit {
  background: #3498db;
  color: white;
  padding: 15px;
  flex: 2;
  border: none;
  border-radius: 5px;
  font-weight: bold;
}
.btn-cancel {
  background: #555;
  color: white;
  padding: 15px;
  flex: 1;
  border: none;
  border-radius: 5px;
}

.action-btns {
  display: flex;
  gap: 10px;
}
.btn-history {
  background: #3498db;
  color: white;
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 5px;
}
.btn-order {
  background: #42b983;
  color: white;
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 5px;
}
.btn-edit {
  background: #666;
  color: white;
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 5px;
}
.mt-20 {
  margin-top: 20px;
}

/* 기존 주문/내역 관련 스타일 동일... */
.history-table-wrapper {
  max-height: 400px;
  overflow-y: auto;
}
.order-item-card {
  background: #2d2424;
  border: 1px solid #4a2828;
  padding: 15px;
  margin-bottom: 10px;
}
</style>
