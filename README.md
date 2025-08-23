# HowMuch

How much did you cost on each thing?

## We Support

- Android: With Jetpack Compose
- iOS
- PC: With QT framework
- [ ] Harmony OS: We are looking forward to native Ark UI implementations!




---

## Component Framework
Component Framework 旨在通过 Redux 的单向数据流管理，实现业务模块的解耦与状态一致性，避免 ViewModel 之间的直接依赖。

### Component

* 封装业务模块的 **View** 和 **ViewModel**。
* 每个 Component 暴露自己的 **ReduxState**。

实现示例参考`CounterComponent`

### ComponentReceiver

* 负责处理外部 **Action** 与 Component **State** 的同步。
* 可以监听自己的 State 或其他 Component 的 State。

实现示例参考`CounterReceiver`

### ComponentCenter

* 全局唯一的组件中心。
* 管理 Component 与 Receiver 的注册、Redux Store 构建、Action 分发。
* 提供统一的 `dispatch` 和 `useSelector` 方法。

---

### 注册 Component 与 Receiver

```kotlin
val counterComponent = CounterComponent()
val counterReceiver = CounterReceiver()

// 注册业务模块及 Receiver
ComponentCenter.add(counterComponent, counterReceiver)

// 构建全局 Redux Store
ComponentCenter.build()
```

---

### 发送 Action

```kotlin
// 更新计数器
ComponentCenter.dispatch(CounterAction.Increment(1))
ComponentCenter.dispatch(CounterAction.Decrement(2))
```

* Action 会通过 **ComponentCenter** 派发到所有 Receiver。
* Receiver 修改内部 ViewModel 或返回新的 ReduxState。

---
### 在 Receiver 内获取其他Component的数据

```kotlin
observe(CounterState::count) { oldValue, newValue ->
    println("Count changed: $oldValue -> $newValue")
}
```

* 返回值是 **unsubscribe 方法**，可用于取消监听。

---

### Flow 与 State 同步策略

1. **外部 Action** → Receiver → ViewModel 修改 → ReduxState 更新。
2. **内部 ViewModel 更新**（StateFlow emit） → Receiver 派发 `InternalStateUpdated` Action 同步 ReduxState。
3. 避免重复触发：Receiver 对 Increment/Decrement **不直接修改 ReduxState**，而由 Flow emit 更新。
