(ns lua.torch
  (:require [std.lib :as h]
            [std.lang :as l]
            [std.string :as str]
            [xt.lang.base-repl :as repl])
  (:refer-clojure :exclude [cat chunk class load max min
                            mod rand range sort test type test]))

(l/script :lua
  {:macro-only true
   :bundle  {:default [["torch" :as torch]]}})

(def +torch+
  '[Allocator
    ByteStorage
    ByteTensor
    CharStorage
    CharTensor
    CmdLine
    DiskFile
    DoubleStorage
    DoubleTensor
    File
    FloatStorage
    FloatTensor
    Generator
    HalfStorage
    HalfTensor
    IntStorage
    IntTensor
    LongStorage
    LongTensor
    MemoryFile
    PipeFile
    ShortStorage
    ShortTensor
    Storage
    Tensor
    TestSuite
    Tester
    Timer
    _gen
    _heaptracking
    abs
    acos
    add
    addbmm
    addcdiv
    addcmul
    addmm
    addmv
    addr
    all
    any
    asin
    atan
    atan2
    baddbmm
    bernoulli
    bhistc
    bitand
    bitor
    bitxor
    bmm
    cat
    cauchy
    cbitand
    cbitor
    cbitxor
    cdata
    cdiv
    ceil
    cfmod
    chunk
    cinv
    clamp
    class
    clshift
    cmax
    cmin
    cmod
    cmul
    conv2
    conv3
    cos
    cosh
    cpow
    cremainder
    cross
    crshift
    csub
    cumprod
    cumsum
    data
    deserialize
    deserializeFromStorage
    diag
    dist
    div
    dot
    eig
    eq
    equal
    exp
    expand
    expandAs
    exponential
    eye
    factory
    fill
    floor
    fmod
    frac
    gather
    ge
    gels
    geometric
    geqrf
    ger
    gesv
    getRNGState
    getconstructortable
    getdefaulttensortype
    getenv
    getmetatable
    getnumcores
    getnumthreads
    gt
    histc
    include
    initialSeed
    inverse
    isStorage
    isTensor
    isTypeOf
    isatty
    isequal
    kthvalue
    le
    lerp
    linspace
    load
    loadobj
    log
    log1p
    logNormal
    logspace
    lshift
    lt
    manualSeed
    match
    max
    mean
    median
    metatype
    min
    mm
    mod
    mode
    mul
    multinomial
    multinomialAlias
    multinomialAliasSetup
    multinomialAliasSetup_
    multinomialAlias_
    mv
    ne
    neg
    newmetatable
    nonzero
    norm
    normal
    numel
    ones
    orgqr
    ormqr
    packageLuaPath
    permute
    pointer
    potrf
    potri
    potrs
    pow
    prod
    pstrf
    pushudata
    qr
    rand
    randn
    random
    randperm
    range
    remainder
    renorm
    repeatTensor
    reshape
    round
    rshift
    rsqrt
    save
    saveobj
    scatter
    seed
    serialize
    serializeToStorage
    setRNGState
    setdefaulttensortype
    setenv
    setheaptracking
    setmetatable
    setnumthreads
    sigmoid
    sign
    sin
    sinh
    sort
    split
    sqrt
    squeeze
    std
    sum
    svd
    symeig
    tan
    tanh
    test
    tic
    toc
    topk
    totable
    trace
    tril
    triu
    trtrs
    trunc
    type
    typename
    uniform
    updateerrorhandlers
    updatethreadlocals
    var
    version
    view
    viewAs
    xcorr2
    xcorr3
    zero
    zeros])

(h/template-entries [l/tmpl-entry {:type :fragment
                                   :base "torch"
                                   :tag "lua"
                                   :shrink true}]
  +torch+)


(comment
  (!.lua
   (-/manualSeed 1234)
   (var A (-/rand 2 2))
   [(. (* A A) (totable))
    (. (* A A) (t) (totable))])

  
  (!.lua
   ((. (require "ffi")
       abi) "eabi"))
  
  (!.lua (+ 1 2 3)))
