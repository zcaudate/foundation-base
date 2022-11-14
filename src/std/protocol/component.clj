(ns std.protocol.component)

(defprotocol IComponent
  (-start         [component])
  (-stop          [component])
  (-kill          [component]))

(defprotocol IComponentQuery
  (-started?      [component])
  (-stopped?      [component])
  (-info          [component level])
  (-remote?       [component])
  (-health        [component]))

(defprotocol IComponentProps
  (-props     [component]))

(defprotocol IComponentOptions
  (-get-options   [component]))

(defprotocol IComponentTrack
  (-get-track-path  [component]))
