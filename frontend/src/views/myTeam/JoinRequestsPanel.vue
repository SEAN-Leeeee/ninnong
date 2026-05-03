<template>
  <div class="join-requests-panel">
    <h3 class="panel-title">가입 요청</h3>

    <div v-if="isLoading" class="state-msg">불러오는 중...</div>

    <div v-else-if="applications.length === 0" class="state-msg">
      아직 가입 요청이 없습니다.
    </div>

    <table v-else class="req-table">
      <thead>
        <tr>
          <th>이름</th>
          <th>선출</th>
          <th>신청 메시지</th>
          <th>신청일</th>
          <th>상태</th>
          <th>처리</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="app in applications"
          :key="app.applicantId"
          class="clickable-row"
          @click="openModal(app)"
        >
          <td class="t-name">{{ app.name }}</td>
          <td class="t-center">
            <span class="badge badge-draft">{{ draftLabel(app.draftLevel) }}</span>
          </td>
          <td class="t-msg">{{ app.requestMsg || '-' }}</td>
          <td class="t-center t-date">{{ formatDate(app.requestAt) }}</td>
          <td class="t-center">
            <span class="status-badge" :class="statusClass(app.status)">
              {{ statusLabel(app.status) }}
            </span>
          </td>
          <td class="t-center t-actions" @click.stop>
            <template v-if="app.status === 'PENDING'">
              <button class="btn-accept" @click="respond(app, 'ACCEPT')">수락</button>
              <button class="btn-reject" @click="respond(app, 'REJECTED')">거절</button>
            </template>
            <span v-else-if="app.status === 'ACCEPT'" class="decision-label decision-accept">수락됨</span>
            <span v-else-if="app.status === 'REJECTED'" class="decision-label decision-reject">거절됨</span>
            <span v-else class="t-done">-</span>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 메시지 모달 -->
    <div v-if="selectedApp" class="modal-backdrop" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <span class="modal-title">신청 메시지</span>
          <span class="modal-name">{{ selectedApp.name }}</span>
          <span class="modal-meta">{{ formatDate(selectedApp.requestAt) }}</span>
        </div>
        <div class="modal-body">
          <p class="modal-msg">{{ selectedApp.requestMsg || '작성된 메시지가 없습니다.' }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn-close" @click="closeModal">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/axios.js';

export default {
  name: 'JoinRequestsPanel',
  props: {
    teamId: { type: Number, required: true },
    isLeader: { type: Boolean, default: false },
  },
  data() {
    return {
      applications: [],
      isLoading: false,
      selectedApp: null,
    };
  },
  mounted() {
    this.loadApplications();
  },
  methods: {
    async loadApplications() {
      this.isLoading = true;
      try {
        const res = await api.get(`/teamApplication/${this.teamId}`);
        this.applications = res.data;
      } catch (e) {
        console.error('가입 요청 목록 조회 실패', e);
      } finally {
        this.isLoading = false;
      }
    },
    async respond(app, decision) {
      try {
        await api.patch(`/teamApplication/${this.teamId}`, {
          applicantId: app.applicantId,
          decision,
          responseMsg: '',
        });
        await this.loadApplications();
      } catch (e) {
        console.error('처리 실패', e);
        alert('처리 중 오류가 발생했습니다.');
      }
    },
    openModal(app) {
      this.selectedApp = app;
    },
    closeModal() {
      this.selectedApp = null;
    },
    draftLabel(level) {
      const map = { ELEMENTARY: '초등부', MIDDLE: '중등부', HIGH: '고등부', UNIVERSITY: '대학부', NONE: '해당없음' };
      return map[level] || level;
    },
    statusLabel(status) {
      const map = { PENDING: '대기중', ACCEPT: '수락', REJECTED: '거절', CANCELED: '취소' };
      return map[status] || status;
    },
    statusClass(status) {
      return { PENDING: 'status-pending', ACCEPT: 'status-accept', REJECTED: 'status-reject', CANCELED: 'status-cancel' }[status] || '';
    },
    formatDate(dt) {
      if (!dt) return '-';
      return new Date(dt).toLocaleString('ko-KR', { dateStyle: 'short', timeStyle: 'short' });
    },
  },
};
</script>

<style scoped>
.join-requests-panel {
  font-family: 'Pretendard', -apple-system, sans-serif;
}

.panel-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.state-msg {
  padding: 40px 0;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}

/* ── Table ── */
.req-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.req-table thead th {
  padding: 10px 12px;
  text-align: center;
  font-weight: 600;
  color: #6b7280;
  border-bottom: 2px solid #e5e7eb;
  white-space: nowrap;
}

.req-table tbody tr {
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.15s;
}
.clickable-row {
  cursor: pointer;
}
.req-table tbody tr:hover {
  background: #f9fafb;
}
.req-table tbody tr:last-child {
  border-bottom: none;
}

.req-table td {
  padding: 12px 12px;
  vertical-align: middle;
}

.t-center { text-align: center; }
.t-name   { font-weight: 600; color: #111827; }
.t-msg    { color: #4b5563; max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.t-date   { color: #6b7280; font-size: 13px; white-space: nowrap; }
.t-done   { color: #d1d5db; }

/* ── Badges ── */
.badge-draft {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  background: #eff6ff;
  color: #3b82f6;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}
.status-pending { background: #fef9c3; color: #854d0e; }
.status-accept  { background: #dcfce7; color: #166534; }
.status-reject  { background: #fee2e2; color: #991b1b; }
.status-cancel  { background: #f3f4f6; color: #6b7280; }

/* ── Action buttons ── */
.t-actions {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.btn-accept,
.btn-reject {
  padding: 5px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.15s;
}
.btn-accept:hover,
.btn-reject:hover { opacity: 0.8; }

.btn-accept { background: #3b82f6; color: #fff; }
.btn-reject { background: #f3f4f6; color: #374151; border: 1px solid #e5e7eb; }

.decision-label {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
}
.decision-accept { background: #dcfce7; color: #166534; }
.decision-reject { background: #fee2e2; color: #991b1b; }

/* ── Modal ── */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  border-radius: 12px;
  width: 460px;
  max-width: 90vw;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.16);
  overflow: hidden;
}

.modal-header {
  padding: 20px 24px 12px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.modal-title {
  font-size: 13px;
  font-weight: 600;
  color: #3b82f6;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.modal-name {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}

.modal-meta {
  font-size: 12px;
  color: #9ca3af;
}

.modal-body {
  padding: 20px 24px;
  min-height: 80px;
}

.modal-msg {
  font-size: 14px;
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
  margin: 0;
}

.modal-footer {
  padding: 12px 24px 20px;
  display: flex;
  justify-content: flex-end;
}

.btn-close {
  padding: 8px 20px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-close:hover {
  background: #e5e7eb;
}
</style>
