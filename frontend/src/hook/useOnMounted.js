import { useEffect, useRef } from "react"

export function useOnMounted(callbackfunc) {
  const initialized = useRef(false)

  useEffect(() => {
    if (!initialized.current) {
      initialized.current = true
      callbackfunc()
    }
  }, [])
}