<template>
  <div class="team-panel mainboard-page attendance-page">
    <h1 class="title">📅 {{ selectedYear }}년 {{ selectedMonth }}월 활동 관리</h1>

    <!-- 월 선택 -->
    <div class="selector">
      <select v-model="selectedYear">
        <option v-for="year in years" :key="year">{{ year }}</option>
      </select>
      <select v-model="selectedMonth">
        <option v-for="month in months" :key="month" :value="month">{{ month }}월</option>
      </select>
    </div>

    <!-- + 버튼으로 활동 추가창 열기 -->
    <div class="toggle-add-button" @click="toggleForm = !toggleForm">
      <span v-if="!toggleForm">➕ 활동 추가하기</span>
      <span v-else>🔽 활동 입력 닫기</span>
    </div>

    <!-- 활동 입력 -->
    <div v-if="toggleForm" class="input-box">
      <h2>📝 활동 추가</h2>
      <div class="input-split">
        <!-- 왼쪽: 날짜/장소/유형/메모 -->
        <div class="input-left">
          <input type="date" v-model="form.date" />
          <select v-model="form.type">
            <option disabled value="">활동 유형 선택</option>
            <option>연습</option>
            <option>경기</option>
            <option>회식</option>
          </select>
          <input type="text" v-model="form.location" placeholder="장소 입력" />
          <textarea v-model="form.memo" placeholder="메모 입력 (선택)" />
        </div>

        <!-- 오른쪽: 참석자 선택 -->
        <div class="input-right">
          <div class="member-scroll-box">
            <label v-for="member in members" :key="member">
              <input type="checkbox" :value="member" v-model="form.attendees" />
              {{ member }}
            </label>
          </div>
        </div>
      </div>
      <button @click="addActivity">+ 활동 저장</button>
    </div>

    <!-- 활동 목록 (modern table) -->
    <div v-if="filteredActivities.length > 0" class="activity-list">
      <h2 class="list-title">📋 활동 목록</h2>
      <table class="member-manage-table modern attendance-table">
        <thead>
          <tr>
            <th>날짜</th>
            <th>유형</th>
            <th>장소</th>
            <th>참석자</th>
            <th>메모</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="activity in filteredActivities" :key="activity.id">
            <td><div class="cell-wrap mono">{{ formatDate(activity.date) }}</div></td>
            <td><div class="cell-wrap">{{ activity.type }}</div></td>
            <td><div class="cell-wrap">{{ activity.location }}</div></td>
            <td>
              <div class="cell-wrap attendees-cell">
                <span class="attendee-chip" v-for="(m, i) in limitedAttendees(activity.attendees)" :key="i">{{ m }}</span>
                <span v-if="activity.attendees.length > attendeeLimit" class="more-chip">+{{ activity.attendees.length - attendeeLimit }}</span>
              </div>
            </td>
            <td>
              <div class="cell-wrap memo-cell">
                <span class="memo-text">{{ activity.memo || '-' }}</span>
              </div>
            </td>
            <td class="actions-cell">
              <button class="icon-btn" @click="editActivity(activity)" title="수정">
                <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 113 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
              </button>
              <button class="icon-btn danger" @click="deleteActivity(activity.id)" title="삭제">
                <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M3 6h18"/><path d="M8 6v14a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else class="no-activity">이번 달 활동이 없습니다.</div>
  </div>
</template>

<script>
export default {
  name: 'AttendanceManager',
  data() {
    return {
      selectedYear: new Date().getFullYear(),
      selectedMonth: new Date().getMonth() + 1,
      years: [2024, 2025, 2026],
      months: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
      members: ['김코딩', '박개발', '이프로그', '최디버깅'],
      attendeeLimit: 3,
      activities: [],
      toggleForm: false,
      form: {
        id: null,
        date: '',
        type: '',
        location: '',
        memo: '',
        attendees: []
      }
    };
  },
  computed: {
    filteredActivities() {
      return this.activities.filter(activity => {
        const d = new Date(activity.date);
        return (
            d.getFullYear() === this.selectedYear &&
            d.getMonth() + 1 === this.selectedMonth
        );
      });
    }
  },
  methods: {
    addActivity() {
      if (!this.form.date || !this.form.type || !this.form.location || this.form.attendees.length === 0) {
        alert('날짜, 유형, 장소, 참석자를 모두 입력해주세요.');
        return;
      }
      if (this.form.id) {
        const idx = this.activities.findIndex(a => a.id === this.form.id);
        if (idx !== -1) this.activities.splice(idx, 1, { ...this.form });
      } else {
        const newActivity = { ...this.form, id: Date.now() };
        this.activities.push(newActivity);
      }
      this.resetForm();
      this.toggleForm = false;
    },
    editActivity(activity) {
      this.form = { ...activity };
      this.toggleForm = true;
    },
    deleteActivity(id) {
      this.activities = this.activities.filter(a => a.id !== id);
      if (this.form.id === id) this.resetForm();
    },
    resetForm() {
      this.form = {
        id: null,
        date: '',
        type: '',
        location: '',
        memo: '',
        attendees: []
      };
    },
    formatDate(dateStr) {
      const d = new Date(dateStr);
      return `${d.getMonth() + 1}월 ${d.getDate()}일 (${['일', '월', '화', '수', '목', '금', '토'][d.getDay()]})`;
    },
    limitedAttendees(list) {
      if (!Array.isArray(list)) return [];
      return list.slice(0, this.attendeeLimit);
    }
  }
};
</script>
