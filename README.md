# foundation

### prerequisites

Please see setup for [the testing environment](https://github.com/zcaudate/infra-testing/blob/main/infra/Dockerfile_foundation) which builds the docker container that is running the [base tests](https://github.com/zcaudate/foundation-ci/actions/workflows/test-base.yml). VNC is not needed for desktop testing.

### installation

To run all test:

> git clone git@github.com:zcaudate/foundation-base.git
> cd foundation-base
> lein test

Currently clojars deploy is broken. To install jars to `.m2` for use in other projects run:

> lein install

### std.lang - overview

[std.lang](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang.clj) started off as an experimental snippet generator to run bits of lua code on openresty. I was looking around for a lightweight alternative to clojure servers and made the decision because of [this epic rant](https://github.com/zcaudate/foundation-base/discussions/4) on Quora. 

As features crept into the library, it slowly evolved into what it is now. More features were added to the project and it has evolved into a tool for exploration into multi-language/multi-runtime environment, allowing unprecedented control in code manipulation and testing:

- A standard set of [symbols](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/base/grammer_spec.clj) for transpile
- grammer spec for transpile to the following target languages:
  - [lua](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/model/spec_lua.clj)       (production ready)
  - [js](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/model/spec_js.clj)        (production ready)
  - [python](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/model/spec_python.clj)    (somewhat production ready)
  - [r](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/model/spec_r.clj)         (experimetal)
  - [c](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/model/spec_c.clj)         (gpu kernel code)
  - [solidity](https://github.com/zcaudate/foundation-base/blob/main/src/rt/solidity/grammer.clj)  (production ready)
  - [bash](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/model/spec_bash.clj)      (experimental)
  - go        (yet todo)
  - ocaml     (yet todo)
  - haskell   (yet todo)
- Live eval through pluggable runtimes
  - os      ([js](https://github.com/zcaudate/foundation-base/blob/main/src/rt/basic/impl/process_js.clj), [python](https://github.com/zcaudate/foundation-base/blob/main/src/rt/basic/impl/process_python.clj), [R](https://github.com/zcaudate/foundation-base/blob/main/src/rt/basic/impl/process_r.clj), [lua](https://github.com/zcaudate/foundation-base/blob/main/src/rt/basic/impl/process_lua.clj), [c](https://github.com/zcaudate/foundation-base/blob/main/src/rt/basic/impl/process_c.clj))  
  - [graal](https://github.com/zcaudate/foundation-base/blob/main/src/rt/graal.clj)   (js, python)
  - [nginx](https://github.com/zcaudate/foundation-base/blob/main/src/rt/nginx.clj)   (lua)
  - [evm](https://github.com/zcaudate/foundation-base/blob/main/src/rt/solidity/client.clj)     (solidity)
  - jocl        (c, gpu kernel)  
  - websockets  (js, lua, python)
  - browser     (js, through chrome driver)
  - blender     (python, yet todo)
- [xtalk](https://github.com/zcaudate/foundation-base/blob/main/src/std/lang/base/grammer_xtalk.clj) (crosstalk), a template language transpiling across dynamic language targets (lua, js, python)
- various helpers across different environments
- [js](https://github.com/zcaudate/foundation-base/tree/main/src/js), [lua](https://github.com/zcaudate/foundation-base/tree/main/src/lua) and [xtalk](https://github.com/zcaudate/foundation-base/tree/main/src/xt) utility libaries.

### std.lang - walkthroughs

Guided walkthroughs are provided for 
- [00 basics](https://github.com/zcaudate/foundation-base/blob/main/src-build/walkthrough/std_lang_00_basic.clj)
- [01 multi-lang](https://github.com/zcaudate/foundation-base/blob/main/src-build/walkthrough/std_lang_01_multi.clj)
- [02 live eval](https://github.com/zcaudate/foundation-base/blob/main/src-build/walkthrough/std_lang_02_live.clj)
- 03 under the hood
- 04 creating a language
- 05 creating a runtime

## License

Copyright © 2022 Chris Zheng

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
