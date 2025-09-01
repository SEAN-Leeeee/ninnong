<template>
  <aside class="side-menu">
    <ul>
      <li><button>전체</button></li>
      <li><button>자유</button></li>
      <li><button>취업·자격증</button></li>
      <li><button>공고제안</button></li>
      <li><button>현직자</button></li>
    </ul>
  </aside>
</template>

<style scoped>
.side-menu {
  width: 240px;
  background-color: #f8f8f8;
  padding: 20px;
  box-shadow: 1px 0 5px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}
.side-menu ul {
  list-style: none;
  padding: 0;
}
.side-menu li {
  margin-bottom: 12px;
}
.side-menu button {
  width: 100%;
  padding: 10px 16px;
  font-size: 16px;
  border: none;
  background-color: #fff;
  border-radius: 8px;
  cursor: pointer;
}
.side-menu button:hover {
  background-color: #eaeaea;
}
</style>
