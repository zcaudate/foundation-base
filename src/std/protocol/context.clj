(ns std.protocol.context)

(defprotocol ISpace
  (-context-set   [sp ctx key options])
  (-context-unset [sp ctx])
  (-context-list  [sp])
  (-context-get   [sp ctx])
  (-rt-active     [sp])
  (-rt-get        [sp ctx])
  (-rt-start      [sp ctx])
  (-rt-started?   [sp ctx])
  (-rt-stopped?   [sp ctx])
  (-rt-stop       [sp ctx]))

(defprotocol IPointer
  (-ptr-context  [_])
  (-ptr-keys     [ptr])
  (-ptr-val      [ptr key]))

(defprotocol IContext
  (-raw-eval          [rt string])
  (-init-ptr          [rt ptr])
  (-tags-ptr          [rt ptr])
  (-deref-ptr         [rt ptr])
  (-display-ptr       [rt ptr])
  (-invoke-ptr        [rt ptr args])
  (-transform-in-ptr  [rt ptr args])
  (-transform-out-ptr [rt ptr return]))

(defprotocol IContextLifeCycle
  (-has-module?       [rt module-id])
  (-setup-module      [rt module-id])
  (-teardown-module   [rt module-id])
  (-has-ptr?          [rt ptr])
  (-setup-ptr         [rt ptr])
  (-teardown-ptr      [rt ptr]))
