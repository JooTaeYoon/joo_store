// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router';

// 1. 컴포넌트들을 임포트합니다.
import CustomerRegistrationForm from '@/view/CustomerRegistrationForm.vue';
import CustomerList from '@/view/CustomerList.vue';
import CustomerDetail from '@/view/CustomerDetail.vue';
import CustomerClothesHistory from '@/view/CustomerClothesHistory.vue';

// 2. 경로(Routes) 정의
const routes = [
  {
    path: '/',
    name: 'Home',
    // 메인 페이지를 손님 목록으로 설정 (가장 자주 사용될 화면)
    redirect: '/customers',
  },
  {
    path: '/register',
    name: 'RegisterCustomer',
    component: CustomerRegistrationForm,
    meta: { title: '손님 등록' },
  },
  {
    path: '/customers',
    name: 'CustomerList',
    component: CustomerList,
    meta: { title: '전체 손님 목록' },
  },
  {
    // 상세 페이지: URL 파라미터(id)를 사용하여 특정 손님을 조회합니다.
    path: '/customers/:id',
    name: 'CustomerDetail',
    component: CustomerDetail,
    // props를 true로 설정하여, URL 파라미터(id)를 컴포넌트의 props로 전달합니다.
    props: true,
    meta: { title: '손님 상세/주문' },
  },
  {
    // 주문/옷 이력 조회 페이지 (필요하다면 별도 경로로 분리)
    path: '/customers/:id/history',
    name: 'ClothesHistory',
    component: CustomerClothesHistory,
    props: true,
    meta: { title: '세탁물 이력' },
  },
];

// 3. 라우터 인스턴스 생성
const router = createRouter({
  // history 모드 사용 (URL에 # 없이 깔끔하게 표시)
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// (선택 사항) 라우트 이동 후 페이지 제목 설정
router.beforeEach((to, from, next) => {
  document.title = to.meta.title
    ? `세탁소 ERP | ${to.meta.title}`
    : '세탁소 ERP';
  next();
});

export default router;
