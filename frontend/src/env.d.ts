/// <reference types="vite/client" />

import type { MessageApiInjection } from 'naive-ui'

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare global {
  interface Window {
    $message?: MessageApiInjection
  }
}
