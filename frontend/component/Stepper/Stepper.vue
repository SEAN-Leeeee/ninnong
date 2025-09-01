<template>
  <div class="outer-container" v-bind="$attrs">
    <div
        class="step-circle-container"
        :class="stepCircleContainerClassName"
        style="border: 1px solid #222"
    >
      <div class="step-indicator-row" :class="stepContainerClassName">
        <template v-for="(_, idx) in stepsArray" :key="idx + 1">
          <component
              :is="renderStepIndicator ? CustomIndicator : StepIndicator"
              :step="idx + 1"
              :current-step="currentStep"
              :disable-step-indicators="disableStepIndicators"
              :on-click-step="onClickStep"
          />
          <StepConnector
              v-if="idx < totalSteps - 1"
              :is-complete="currentStep > idx + 1"
          />
        </template>
      </div>

      <StepContentWrapper
          :is-completed="isCompleted"
          :current-step="currentStep"
          :direction="direction"
          :class="['step-content-default', contentClassName]"
      >
        <component :is="stepsArray[currentStep - 1]" />
      </StepContentWrapper>

      <div v-if="!isCompleted" class="footer-container" :class="footerClassName">
        <div class="footer-nav" :class="currentStep !== 1 ? 'spread' : 'end'">
          <button
              v-if="currentStep !== 1"
              @click="handleBack"
              class="back-button"
              :class="currentStep === 1 ? 'inactive' : ''"
              v-bind="backButtonProps"
          >
            {{ backButtonText }}
          </button>
          <button
              @click="isLastStep ? handleComplete() : handleNext()"
              class="next-button"
              v-bind="nextButtonProps"
          >
            {{ isLastStep ? 'Complete' : nextButtonText }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  h,
  ref,
  computed,
  useSlots,
  watch,
  nextTick,
  Transition,
  Text,
  Comment,
} from 'vue'

/* ===== Props ===== */
const {
  initialStep = 1,
  onStepChange = () => {},
  onFinalStepCompleted = () => {},
  stepCircleContainerClassName = '',
  stepContainerClassName = '',
  contentClassName = '',
  footerClassName = '',
  backButtonProps = {},
  nextButtonProps = {},
  backButtonText = 'Back',
  nextButtonText = 'Continue',
  disableStepIndicators = false,
  renderStepIndicator = null,
} = defineProps({
  initialStep: Number,
  onStepChange: Function,
  onFinalStepCompleted: Function,
  stepCircleContainerClassName: String,
  stepContainerClassName: String,
  contentClassName: String,
  footerClassName: String,
  backButtonProps: Object,
  nextButtonProps: Object,
  backButtonText: String,
  nextButtonText: String,
  disableStepIndicators: Boolean,
  renderStepIndicator: Function,
})

/* ===== Slots → Steps 배열(텍스트/주석 제거) ===== */
const slots = useSlots()
const stepsArray = computed(() => {
  const nodes = slots.default ? slots.default() : []
  return nodes.filter(v => v && v.type !== Text && v.type !== Comment)
})
const totalSteps = computed(() => stepsArray.value.length)

/* ===== State ===== */
const currentStep = ref(initialStep)
const direction = ref(0)
const isCompleted = computed(() => currentStep.value > totalSteps.value)
const isLastStep = computed(() => currentStep.value === totalSteps.value)

/* ===== Navigation ===== */
function updateStep(newStep) {
  currentStep.value = newStep
  if (newStep > totalSteps.value) onFinalStepCompleted?.()
  else onStepChange?.(newStep)
}
function handleBack() {
  if (currentStep.value > 1) {
    direction.value = -1
    updateStep(currentStep.value - 1)
  }
}
function handleNext() {
  if (!isLastStep.value) {
    direction.value = 1
    updateStep(currentStep.value + 1)
  }
}
function handleComplete() {
  direction.value = 1
  updateStep(totalSteps.value + 1)
}

/* ===== Step indicator click ===== */
function onClickStep(clicked) {
  if (clicked === currentStep.value) return
  direction.value = clicked > currentStep.value ? 1 : -1
  updateStep(clicked)
}

/* ===== Custom indicator proxy ===== */
const CustomIndicator = {
  props: ['step', 'currentStep', 'onClickStep', 'disableStepIndicators'],
  setup(p) {
    return () =>
        renderStepIndicator?.({
          step: p.step,
          currentStep: p.currentStep,
          onStepClick: (clicked) => {
            if (!p.disableStepIndicators) onClickStep(clicked)
          },
        })
  },
}

/* ===== StepContentWrapper: 높이 전환 + 슬라이드 ===== */
const StepContentWrapper = {
  props: ['isCompleted', 'currentStep', 'direction'],
  setup(props, { slots, attrs }) {
    const wrap = ref(null)
    const viewKey = ref(props.currentStep)
    const childHeight = ref(0)

    const measure = async () => {
      await nextTick()
      const el = wrap.value?.querySelector('[data-step-slide]')
      childHeight.value = props.isCompleted ? 0 : (el?.offsetHeight || 0)
    }

    watch(
        () => [props.currentStep, props.isCompleted],
        async () => {
          viewKey.value = props.currentStep
          await measure()
        },
        { immediate: true }
    )

    const onEnter = (el, done) => {
      el.style.position = 'absolute'
      el.style.left = 0
      el.style.right = 0
      el.style.top = 0
      el.style.transform = `translateX(${props.direction >= 0 ? '-100%' : '100%'})`
      el.style.opacity = '0'
      el.style.transition = 'transform .4s ease, opacity .4s ease'
      requestAnimationFrame(() => {
        el.style.transform = 'translateX(0%)'
        el.style.opacity = '1'
      })
      setTimeout(done, 400)
    }
    const onLeave = (el, done) => {
      el.style.transition = 'transform .4s ease, opacity .4s ease'
      el.style.transform = `translateX(${props.direction >= 0 ? '50%' : '-50%'})`
      el.style.opacity = '0'
      setTimeout(done, 400)
    }

    return () =>
        h(
            'div',
            {
              ref: wrap,
              class: attrs.class,
              style: {
                position: 'relative',
                overflow: 'hidden',
                height: `${childHeight.value}px`,
                transition: 'height .4s cubic-bezier(.2,.8,.2,1)',
              },
            },
            [
              !props.isCompleted &&
              h(
                  Transition,
                  { mode: 'out-in', onEnter, onLeave },
                  {
                    default: () =>
                        h(
                            'div',
                            { key: viewKey.value, 'data-step-slide': '' },
                            slots.default ? slots.default() : []
                        ),
                  }
              ),
            ]
        )
  },
}

/* ===== StepIndicator ===== */
const StepIndicator = {
  props: ['step', 'currentStep', 'onClickStep', 'disableStepIndicators'],
  setup(p) {
    const status = () =>
        p.currentStep === p.step
            ? 'active'
            : p.currentStep < p.step
                ? 'inactive'
                : 'complete'

    const handleClick = () => {
      if (p.step !== p.currentStep && !p.disableStepIndicators) {
        p.onClickStep?.(p.step)
      }
    }

    return () =>
        h('div', { class: 'step-indicator', onClick: handleClick }, [
          h(
              'div',
              {
                class: 'step-indicator-inner',
                style: indicatorStyle(status()),
              },
              status() === 'complete'
                  ? [h(CheckIcon, { class: 'check-icon' })]
                  : status() === 'active'
                      ? [h('div', { class: 'active-dot' })]
                      : [h('span', { class: 'step-number' }, String(p.step))]
          ),
        ])
  },
}
function indicatorStyle(state) {
  if (state === 'inactive')
    return { transform: 'scale(1)', backgroundColor: '#222', color: '#a3a3a3', transition: 'all .3s ease' }
  if (state === 'active')
    return { transform: 'scale(1)', backgroundColor: '#5227FF', color: '#5227FF', transition: 'all .3s ease' }
  return { transform: 'scale(1)', backgroundColor: '#5227FF', color: '#3b82f6', transition: 'all .3s ease' }
}

/* ===== StepConnector ===== */
const StepConnector = {
  props: ['isComplete'],
  setup(p) {
    return () =>
        h('div', { class: 'step-connector' }, [
          h('div', {
            class: 'step-connector-inner',
            style: {
              width: p.isComplete ? '100%' : '0',
              backgroundColor: p.isComplete ? '#5227FF' : 'transparent',
              transition: 'width .4s ease, background-color .4s ease',
            },
          }),
        ])
  },
}

/* ===== CheckIcon ===== */
const CheckIcon = {
  props: ['class'],
  setup(props) {
    return () =>
        h(
            'svg',
            {
              class: props.class,
              fill: 'none',
              stroke: 'currentColor',
              'stroke-width': 2,
              viewBox: '0 0 24 24',
            },
            [
              h('path', {
                'stroke-linecap': 'round',
                'stroke-linejoin': 'round',
                d: 'M5 13l4 4L19 7',
              }),
            ]
        )
  },
}
</script>
