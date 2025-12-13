<template>
  <div id="app">
    <nav class="main-nav">
      <router-link to="/customers">📋 손님 목록</router-link>
      <router-link to="/register">📝 손님 등록</router-link>
    </nav>

    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
// import RegisterView from './view/RegisterView.vue'; // 임포트 필요 없으면 삭제
</script>

<style scoped>
/* 여기에 내비게이션 및 앱 스타일 추가 */
.main-nav {
  /* 스타일 */
}
</style>
