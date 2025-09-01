<template>
  <div class="mainboard-page">
    <SplitText text="멤버 상세정보" />

    <table class="member-manage-table modern">
      <colgroup>
        <col class="w-no" />
        <col class="w-name" />
        <col class="w-pos" />
        <col class="w-draft" />
        <col class="w-date" />
        <col class="w-actions" />
      </colgroup>
      <thead>
        <tr>
          <th class="t-center">등번호</th>
          <th class="t-center">이름</th>
          <th class="t-center">포지션</th>
          <th class="t-center">선출</th>
          <th class="t-center">가입일</th>
          <th class="t-center">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(member, index) in memberList" :key="index">
          <td class="t-center">
            <template v-if="editingIndex === index">
              <input
                  type="text"
                  inputmode="numeric"
                  maxlength="3"
                  placeholder="등번호"
                  @input="handleInputNumber($event, member)"
                  v-model="member.backNumber"
                  class="table-input"
              />
            </template>
            <template v-else>
              <div class="cell-wrap"><span v-if="leader.userId == member.userId">⭐</span>{{ member.backNumber }}</div>
            </template>
          </td>
          <td class="t-center">
            <div class="cell-wrap name-cell"><strong>{{ member.name }}</strong></div>
          </td>
          <td class="t-center">
            <template v-if="editingIndex === index">
              <select v-model="member.position" class="table-select">
                <option
                    v-for="(label, code) in positionOptions"
                    :key="code"
                    :value="code"
                >
                  {{ label }}
                </option>
              </select>
            </template>
            <template v-else>
              <div class="cell-wrap">{{ convertPosition(member.position) }}</div>
            </template>
          </td>
          <td class="t-center">
            <div class="cell-wrap">
              <span class="badge" :class="convertDraftLevel(member.draftLevel) === '해당없음' ? 'badge-ghost' : 'badge-success'">
                {{ convertDraftLevel(member.draftLevel) }}
              </span>
            </div>
          </td>
          <td class="t-center">
            <template v-if="editingIndex === index">
              <input
                  type="date"
                  class="table-input"
                  v-model="member.joinedAt"
                  @input="handleJoinedAtRawInput($event, member)"
                  placeholder="YYYY-MM-DD"
              />
            </template>
            <template v-else>
              <div class="cell-wrap mono">{{ member.joinedAt }}</div>
            </template>
          </td>
          <td class="actions-cell t-center">
            <template v-if="isLeader">
              <template v-if="editingIndex !== index">
                <button class="icon-btn" @click="startEdit(index)" aria-label="Edit row" title="Edit">
                  <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 113 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
                </button>
              </template>
              <template v-else>
                <button class="icon-btn success" @click="saveRow(index)" aria-label="Save row" title="Save">
                  <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M20 6L9 17l-5-5"/></svg>
                </button>
              </template>
            </template>
          </td>
        </tr>
        <tr v-if="memberList.length === 0">
          <td colspan="6">등록된 멤버가 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: "MemberManageBoard",
  props: {
    memberList: {
      type: Array,
      required: false,
      default: () => [],
    },
    leader: {
      type: Object,
      required: false,
    },
    isLeader: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      draftLevelOptions: {
        ELEMENTARY: '초등부',
        MIDDLE: '중등부',
        HIGH: '고등부',
        UNIVERSITY: '대학부',
        NONE: '해당없음',
      },
      positionOptions: {
        GUARD: '가드',
        FORWARD: '포워드',
        CENTER: '센터',
        NONE: '미정',
      },
      editingIndex: null,
      backupRow: null,
    };
  },
  methods: {
    convertDraftLevel(draftLevel) {
      return this.draftLevelOptions[draftLevel] || '해당없음';
    },
    convertPosition(position) {
      return this.positionOptions[position] || '미정';
    },
    handleJoinedAtRawInput(event, member) {
      let raw = event.target.value.replace(/[^\d]/g, '');
      if (raw.length === 8) {
        const formatted = `${raw.slice(0, 4)}-${raw.slice(4, 6)}-${raw.slice(6, 8)}`;
        member.joinedAt = formatted;
      } else {
        member.joinedAt = event.target.value;
      }
    },
    handleInputNumber(event, member) {
      const digits = (event.target.value || '').replace(/\D/g, '');
      member.backNumber = digits;
    },
    startEdit(index) {
      if (!this.isLeader) return;
      this.editingIndex = index;
      this.backupRow = JSON.parse(JSON.stringify(this.memberList[index]));
    },
    saveRow(index) {
      // Emit only the changed row for parent to persist
      const payload = { index, member: JSON.parse(JSON.stringify(this.memberList[index])) };
      this.$emit('save-member', payload);
      this.editingIndex = null;
      this.backupRow = null;
    },
    cancelRow(index) {
      if (this.backupRow) {
        this.$set(this.memberList, index, JSON.parse(JSON.stringify(this.backupRow)));
      }
      this.editingIndex = null;
      this.backupRow = null;
    },
  }
};
</script>
