(ns std.image.awt
  (:require [std.image.awt.common :as common]
            [std.image.awt.display :as display]
            [std.image.awt.io :as io]
            [std.image.protocol :as protocol.image])
  (:import (java.awt.image BufferedImage)))

(extend-protocol protocol.image/IRepresentation
  BufferedImage
  (-channels [img] (common/image-channels img))
  (-size     [img] (common/image-size img))
  (-model    [img] (common/image-model img))
  (-data     [img] (common/image-data img))
  (-subimage [img x y w h]
    (common/subimage img x y w h)))

(defmethod print-method BufferedImage
  ([^BufferedImage v ^java.io.Writer w]
   (.write w (str "#image.awt" [(.getWidth v) (.getHeight v)]
                  {:model (-> (protocol.image/-model v) :label)}))))

(defmethod protocol.image/-image BufferedImage
  ([size model data _]
   (common/image size model data)))

(defmethod protocol.image/-image :awt
  ([size model data _]
   (common/image size model data)))

(defmethod protocol.image/-blank BufferedImage
  ([size model _]
   (common/image size model nil)))

(defmethod protocol.image/-blank :awt
  ([size model _]
   (common/image size model nil)))

(defmethod protocol.image/-read BufferedImage
  ([source model _]
   (io/read source model)))

(defmethod protocol.image/-read :awt
  ([source model _]
   (io/read source model)))

(extend-protocol protocol.image/ITransfer
  BufferedImage
  (-to-byte-gray [image]
    (common/image-to-byte-gray image))
  (-to-int-argb  [image]
    (common/image-to-int-argb image))
  (-write   [image sink opts]
    (io/write image sink opts)))

(defmethod protocol.image/-display BufferedImage
  ([image opts _]
   (display/display image opts)))

(defmethod protocol.image/-display-class BufferedImage
  ([_]
   #{BufferedImage}))

(defmethod protocol.image/-display :awt
  ([image opts _]
   (display/display image opts)))

(defmethod protocol.image/-display-class :awt
  ([_]
   #{BufferedImage}))
