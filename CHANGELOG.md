# 1.0.0 (2022-08-12)


### Bug Fixes

* add type annotations to silence wartremover ([0f2e14c](https://github.com/atedeg/mdm/commit/0f2e14c94e479848e2f83052d48145db449a353c))
* change endpoint path parameter name ([7290b6a](https://github.com/atedeg/mdm/commit/7290b6a53dce5082f2737b50c71cb759133b9497))
* date dto in prod planning acl ([aad6bff](https://github.com/atedeg/mdm/commit/aad6bfffabedd03b771e2cb4d7d837fbebde09cb))
* fix date time formatter ([73de3fd](https://github.com/atedeg/mdm/commit/73de3fdff5e37030aa2006e3073e2e0aa7dc0a1c))
* fix format string ([cafb5be](https://github.com/atedeg/mdm/commit/cafb5be9fbf252a06e1e93dbb59269248d4f783f))
* fix ubidoc table ([6b8b0f9](https://github.com/atedeg/mdm/commit/6b8b0f9b152f5c526163116ddb0cfc551e091e2c))
* in handleOrderReceived use order instead of ordered product ([facbaba](https://github.com/atedeg/mdm/commit/facbaba0a5425d995487316220dfd0088262efe8))
* location uses decimals ([e58c814](https://github.com/atedeg/mdm/commit/e58c81436ce1bca5ab75a377262c3e59afa95737))
* **products:** change macro to generate list of weights per cheese type ([038da8b](https://github.com/atedeg/mdm/commit/038da8b78adf337c0bc34ad06bb224abceaed0a7))
* progate date error in prod plan acl ([3740f91](https://github.com/atedeg/mdm/commit/3740f91dd846f0b512512dfe4b8fa09ddbdf0687))
* relationships between restocking and production ([04c182b](https://github.com/atedeg/mdm/commit/04c182b7bc2461d1a5908f82ddf3fcc4150c0a74))
* remove private modifier for production planning action ([6913676](https://github.com/atedeg/mdm/commit/69136763fcd2ab8001994a5fdebf08d58706c553))
* replace quantity with missing quantity ([8fa1f2d](https://github.com/atedeg/mdm/commit/8fa1f2d10f3c140eb0f98234793708073fb24a7b))
* use format instead of to string ([330bd02](https://github.com/atedeg/mdm/commit/330bd0211bfeb1d6ebe51bccddfa727b6db4683a))
* use to string for local date time conversionù ([cc57cca](https://github.com/atedeg/mdm/commit/cc57cca49b8dde3d9ba8ff705e23dc09c1d52222))
* **utils:** move givens from outside compatnion objects and add Distance type class ([64cde37](https://github.com/atedeg/mdm/commit/64cde377efc26a844ce93899390df5939f0ec9f6))


### Features

* add acl dtos ([d85d6c2](https://github.com/atedeg/mdm/commit/d85d6c2eb4ecbd1df20ec3b9f941ee1283a6d9a6))
* add client orders errors ([7b0eb54](https://github.com/atedeg/mdm/commit/7b0eb5464032dd5f91f43ee7d5a1bc8c2f3b6f69))
* add client orders events ([fd66d84](https://github.com/atedeg/mdm/commit/fd66d8422201db21d56a1675a44ab25d1eef8473))
* add client orders types ([0167e81](https://github.com/atedeg/mdm/commit/0167e8139855e656517ea89422bedad5a6ef228e))
* add desired stock to dto stock dto in prod plan acl ([9b4c9c5](https://github.com/atedeg/mdm/commit/9b4c9c559d893851760a16496b6ba7f539cad0ab))
* add domain actions and events, closes [#77](https://github.com/atedeg/mdm/issues/77) ([5d50f12](https://github.com/atedeg/mdm/commit/5d50f1221cb6fe5348a711cba2d5d3b0edb81c66))
* add DTOs for milk-planning BC ([84ff35c](https://github.com/atedeg/mdm/commit/84ff35ce01367bc561c09db785170f5691f28ac2))
* add DTOs for product and cheesetype ([bf3711a](https://github.com/atedeg/mdm/commit/bf3711a0fdc9bc6e9c98f0bc068d241b9643b737))
* add DTOs for product and cheesetype ([5f1c40a](https://github.com/atedeg/mdm/commit/5f1c40ad23c26a15b9904f4e02740f77c90d44f4))
* add endpoint to palletize product for order ([c1b1afd](https://github.com/atedeg/mdm/commit/c1b1afdc1fc63dd5e7940e39d46645e67cecde66))
* add endpoint to place new orders ([5a6c2b0](https://github.com/atedeg/mdm/commit/5a6c2b03eb05eeef0673ceaa41cf170c14e488cd))
* add endpoints ([938dae6](https://github.com/atedeg/mdm/commit/938dae62581a74aca245b689f074ef3cda9e4517))
* add endpoints to complete orders and get their transport document ([27973fe](https://github.com/atedeg/mdm/commit/27973fed4ec344b5827cb6dbf6e396813b24fac8))
* add first draft implementation of one of the actions ([474ce44](https://github.com/atedeg/mdm/commit/474ce44f9d7c849307775cd233fe22bc55fc5e81))
* add handler for incoming orders ([c076741](https://github.com/atedeg/mdm/commit/c07674154332e641e8adec4c2a85d80d71e55707))
* add handlers ([ed8a27d](https://github.com/atedeg/mdm/commit/ed8a27d634df701d5a51125b2823e2b75fae28d1))
* add MissingQuantity class ([e7a23f7](https://github.com/atedeg/mdm/commit/e7a23f72e9c4babdcd89e9688fa5539cc59e8e53))
* add new batch event ([9c0f30f](https://github.com/atedeg/mdm/commit/9c0f30fed2e8192ce96b1aa86f082c5cdf0c6dfa))
* add new delivery date for delayed orded in production planning bc ([b14b551](https://github.com/atedeg/mdm/commit/b14b551de76f7a01f27da18161f3cd7eee728691))
* add new outgoing event ([498bb39](https://github.com/atedeg/mdm/commit/498bb39de6160c2edf224d0eaccc57b3c9d83510))
* add order received and production plan send handlers ([2893046](https://github.com/atedeg/mdm/commit/2893046c2221774f577cd824e784853f77f31ea3))
* add prod planning dtos ([c41bcea](https://github.com/atedeg/mdm/commit/c41bcea4af51fa5814369fa025c2de5eec298b2d))
* add product palletized event to client orders bc ([4b8474c](https://github.com/atedeg/mdm/commit/4b8474c797f54bc8e7ddb7b25df37fd82844cfea))
* add production DTOs ([c146b48](https://github.com/atedeg/mdm/commit/c146b489d0fbdaf803bc415c4520f00ae16450c6))
* add production planning DTOs ([d3ae700](https://github.com/atedeg/mdm/commit/d3ae700dc71a14edb7a2853d998f6e7ecea944c4))
* add repositories ([344f266](https://github.com/atedeg/mdm/commit/344f2666adeefc268b16d7ab6d98f08ce94bed68))
* add restocking bc ([36af124](https://github.com/atedeg/mdm/commit/36af124611279107621dca88931483f72eb8ec81))
* add stock repository ([6d62e93](https://github.com/atedeg/mdm/commit/6d62e93c119e25a592e896373df813df67b14782))
* add StockDTO ([cffaa9f](https://github.com/atedeg/mdm/commit/cffaa9f4bb42e000e27aa4905daeb6c47b0c59c5))
* add stocking bounded context ([028ee56](https://github.com/atedeg/mdm/commit/028ee56eff798fa7a6f63eabdb19e77c7ace4b79))
* add ubiquitous language,closes [#75](https://github.com/atedeg/mdm/issues/75) ([6cfe906](https://github.com/atedeg/mdm/commit/6cfe906cd70361719c9c24dabc676157b2b4e976))
* available stock and desired stock are now case classes ([44ec364](https://github.com/atedeg/mdm/commit/44ec36436e2cc05b3ec604b6413529bea9c0b62b))
* bc actions definition ([984a7ba](https://github.com/atedeg/mdm/commit/984a7ba6e6813d73e7285e4d6c4af9e756e645b2))
* bc events definition ([1ae213e](https://github.com/atedeg/mdm/commit/1ae213e4af772da429d072941a7844c7838a0a12))
* bc types definition ([fbdcd83](https://github.com/atedeg/mdm/commit/fbdcd837a169d53e414810b74003d6672d0118b4))
* complete domain modelling ([956703d](https://github.com/atedeg/mdm/commit/956703d9a4144d5e95dec5264ef4e179f0ee4626))
* complete quintals of milk estimation ([34aa275](https://github.com/atedeg/mdm/commit/34aa275fb14bba2a3264679d4f7b72f67e592fbb))
* create logic handlers ([16b6648](https://github.com/atedeg/mdm/commit/16b6648ac9c1680529e4e6bee089be02f41448e4))
* creates api endpoints specifications ([6ff71ab](https://github.com/atedeg/mdm/commit/6ff71ab44f86aa59df6049928f6dd115c452b60a))
* define DTOs for restocking b.c. ([8035a9c](https://github.com/atedeg/mdm/commit/8035a9c0e4b74ad10eb5b265ea10baa9d63accbc))
* define first batch of ubiquitous language ([fa83eee](https://github.com/atedeg/mdm/commit/fa83eeeec89b037a7b3c20ef0e71828bd2039140))
* define incoming and outgoing events ([a2e0174](https://github.com/atedeg/mdm/commit/a2e01748f7a94c7d2117a44e10c7ca048f3690bb))
* definition of domain events for milk-planning ([242a875](https://github.com/atedeg/mdm/commit/242a875a1684d22ace87ba51d04b4a5b1589c790))
* emit processed order event ([dac6483](https://github.com/atedeg/mdm/commit/dac64834f8bae9b8271f935b70f61ca5a46cb2ff))
* first basic implementation for estimate quintals of milk ([697e87d](https://github.com/atedeg/mdm/commit/697e87df07da321d200e29d499396b362743f9cf))
* first definition of domain actions ([fcdcbbf](https://github.com/atedeg/mdm/commit/fcdcbbf16818d8103e595fd9f44476f77e2fa0be))
* implement bc actions ([86c6e86](https://github.com/atedeg/mdm/commit/86c6e869432d3def47e7ff6f0a3bbbb63e4c3f5f))
* implement client-order actions ([a0f6240](https://github.com/atedeg/mdm/commit/a0f624037198bbff0fd3102936122e6165aba943))
* implement logic for calculate milk needed for products ([d914fe7](https://github.com/atedeg/mdm/commit/d914fe7bafa9211be938c4bd61d064bf8f9774e0))
* implement milk-planning API ([640f00c](https://github.com/atedeg/mdm/commit/640f00c651b20847bef97a8ff0b15a277c26f45b))
* implement stocking actions ([7cbe9c0](https://github.com/atedeg/mdm/commit/7cbe9c0fa2a6dc7b2ecdfbba9a32d16bdcb17695))
* save the production plan ([3457eaa](https://github.com/atedeg/mdm/commit/3457eaae70eb52288aaaf5df77593e2435c8c288))
* **stocking:** integrate stocking bounded context with shared kernel ([099bc05](https://github.com/atedeg/mdm/commit/099bc05fd440dc3c582e1ed849076b57c6ca70a9))

# [1.0.0-beta.16](https://github.com/atedeg/mdm/compare/v1.0.0-beta.15...v1.0.0-beta.16) (2022-08-11)


### Bug Fixes

* date dto in prod planning acl ([aad6bff](https://github.com/atedeg/mdm/commit/aad6bfffabedd03b771e2cb4d7d837fbebde09cb))
* in handleOrderReceived use order instead of ordered product ([facbaba](https://github.com/atedeg/mdm/commit/facbaba0a5425d995487316220dfd0088262efe8))
* progate date error in prod plan acl ([3740f91](https://github.com/atedeg/mdm/commit/3740f91dd846f0b512512dfe4b8fa09ddbdf0687))


### Features

* add desired stock to dto stock dto in prod plan acl ([9b4c9c5](https://github.com/atedeg/mdm/commit/9b4c9c559d893851760a16496b6ba7f539cad0ab))
* add order received and production plan send handlers ([2893046](https://github.com/atedeg/mdm/commit/2893046c2221774f577cd824e784853f77f31ea3))
* save the production plan ([3457eaa](https://github.com/atedeg/mdm/commit/3457eaae70eb52288aaaf5df77593e2435c8c288))

# [1.0.0-beta.15](https://github.com/atedeg/mdm/compare/v1.0.0-beta.14...v1.0.0-beta.15) (2022-08-11)


### Bug Fixes

* change endpoint path parameter name ([7290b6a](https://github.com/atedeg/mdm/commit/7290b6a53dce5082f2737b50c71cb759133b9497))


### Features

* add endpoint to palletize product for order ([c1b1afd](https://github.com/atedeg/mdm/commit/c1b1afdc1fc63dd5e7940e39d46645e67cecde66))
* add endpoint to place new orders ([5a6c2b0](https://github.com/atedeg/mdm/commit/5a6c2b03eb05eeef0673ceaa41cf170c14e488cd))
* add endpoints to complete orders and get their transport document ([27973fe](https://github.com/atedeg/mdm/commit/27973fed4ec344b5827cb6dbf6e396813b24fac8))
* add handler for incoming orders ([c076741](https://github.com/atedeg/mdm/commit/c07674154332e641e8adec4c2a85d80d71e55707))

# [1.0.0-beta.14](https://github.com/atedeg/mdm/compare/v1.0.0-beta.13...v1.0.0-beta.14) (2022-08-11)


### Bug Fixes

* use format instead of to string ([330bd02](https://github.com/atedeg/mdm/commit/330bd0211bfeb1d6ebe51bccddfa727b6db4683a))
* use to string for local date time conversionù ([cc57cca](https://github.com/atedeg/mdm/commit/cc57cca49b8dde3d9ba8ff705e23dc09c1d52222))


### Features

* add acl dtos ([d85d6c2](https://github.com/atedeg/mdm/commit/d85d6c2eb4ecbd1df20ec3b9f941ee1283a6d9a6))
* add endpoints ([938dae6](https://github.com/atedeg/mdm/commit/938dae62581a74aca245b689f074ef3cda9e4517))
* add handlers ([ed8a27d](https://github.com/atedeg/mdm/commit/ed8a27d634df701d5a51125b2823e2b75fae28d1))
* add product palletized event to client orders bc ([4b8474c](https://github.com/atedeg/mdm/commit/4b8474c797f54bc8e7ddb7b25df37fd82844cfea))
* add repositories ([344f266](https://github.com/atedeg/mdm/commit/344f2666adeefc268b16d7ab6d98f08ce94bed68))
* available stock and desired stock are now case classes ([44ec364](https://github.com/atedeg/mdm/commit/44ec36436e2cc05b3ec604b6413529bea9c0b62b))

# [1.0.0-beta.13](https://github.com/atedeg/mdm/compare/v1.0.0-beta.12...v1.0.0-beta.13) (2022-08-09)


### Bug Fixes

* fix format string ([cafb5be](https://github.com/atedeg/mdm/commit/cafb5be9fbf252a06e1e93dbb59269248d4f783f))


### Features

* implement milk-planning API ([640f00c](https://github.com/atedeg/mdm/commit/640f00c651b20847bef97a8ff0b15a277c26f45b))

# [1.0.0-beta.12](https://github.com/atedeg/mdm/compare/v1.0.0-beta.11...v1.0.0-beta.12) (2022-08-09)


### Features

* add stock repository ([6d62e93](https://github.com/atedeg/mdm/commit/6d62e93c119e25a592e896373df813df67b14782))
* add StockDTO ([cffaa9f](https://github.com/atedeg/mdm/commit/cffaa9f4bb42e000e27aa4905daeb6c47b0c59c5))
* create logic handlers ([16b6648](https://github.com/atedeg/mdm/commit/16b6648ac9c1680529e4e6bee089be02f41448e4))
* creates api endpoints specifications ([6ff71ab](https://github.com/atedeg/mdm/commit/6ff71ab44f86aa59df6049928f6dd115c452b60a))

# [1.0.0-beta.11](https://github.com/atedeg/mdm/compare/v1.0.0-beta.10...v1.0.0-beta.11) (2022-08-08)


### Bug Fixes

* fix date time formatter ([73de3fd](https://github.com/atedeg/mdm/commit/73de3fdff5e37030aa2006e3073e2e0aa7dc0a1c))


### Features

* add DTOs for milk-planning BC ([84ff35c](https://github.com/atedeg/mdm/commit/84ff35ce01367bc561c09db785170f5691f28ac2))
* add DTOs for product and cheesetype ([bf3711a](https://github.com/atedeg/mdm/commit/bf3711a0fdc9bc6e9c98f0bc068d241b9643b737))

# [1.0.0-beta.10](https://github.com/atedeg/mdm/compare/v1.0.0-beta.9...v1.0.0-beta.10) (2022-08-07)


### Features

* add production DTOs ([c146b48](https://github.com/atedeg/mdm/commit/c146b489d0fbdaf803bc415c4520f00ae16450c6))

# [1.0.0-beta.9](https://github.com/atedeg/mdm/compare/v1.0.0-beta.8...v1.0.0-beta.9) (2022-08-07)


### Features

* add DTOs for product and cheesetype ([5f1c40a](https://github.com/atedeg/mdm/commit/5f1c40ad23c26a15b9904f4e02740f77c90d44f4))
* add prod planning dtos ([c41bcea](https://github.com/atedeg/mdm/commit/c41bcea4af51fa5814369fa025c2de5eec298b2d))
* add production planning DTOs ([d3ae700](https://github.com/atedeg/mdm/commit/d3ae700dc71a14edb7a2853d998f6e7ecea944c4))

# [1.0.0-beta.8](https://github.com/atedeg/mdm/compare/v1.0.0-beta.7...v1.0.0-beta.8) (2022-08-06)


### Features

* define DTOs for restocking b.c. ([8035a9c](https://github.com/atedeg/mdm/commit/8035a9c0e4b74ad10eb5b265ea10baa9d63accbc))

# [1.0.0-beta.7](https://github.com/atedeg/mdm/compare/v1.0.0-beta.6...v1.0.0-beta.7) (2022-08-04)


### Bug Fixes

* location uses decimals ([e58c814](https://github.com/atedeg/mdm/commit/e58c81436ce1bca5ab75a377262c3e59afa95737))
* replace quantity with missing quantity ([8fa1f2d](https://github.com/atedeg/mdm/commit/8fa1f2d10f3c140eb0f98234793708073fb24a7b))


### Features

* add client orders errors ([7b0eb54](https://github.com/atedeg/mdm/commit/7b0eb5464032dd5f91f43ee7d5a1bc8c2f3b6f69))
* add client orders events ([fd66d84](https://github.com/atedeg/mdm/commit/fd66d8422201db21d56a1675a44ab25d1eef8473))
* add client orders types ([0167e81](https://github.com/atedeg/mdm/commit/0167e8139855e656517ea89422bedad5a6ef228e))
* add MissingQuantity class ([e7a23f7](https://github.com/atedeg/mdm/commit/e7a23f72e9c4babdcd89e9688fa5539cc59e8e53))
* add new outgoing event ([498bb39](https://github.com/atedeg/mdm/commit/498bb39de6160c2edf224d0eaccc57b3c9d83510))
* emit processed order event ([dac6483](https://github.com/atedeg/mdm/commit/dac64834f8bae9b8271f935b70f61ca5a46cb2ff))
* implement client-order actions ([a0f6240](https://github.com/atedeg/mdm/commit/a0f624037198bbff0fd3102936122e6165aba943))

# [1.0.0-beta.6](https://github.com/atedeg/mdm/compare/v1.0.0-beta.5...v1.0.0-beta.6) (2022-08-04)


### Bug Fixes

* remove private modifier for production planning action ([6913676](https://github.com/atedeg/mdm/commit/69136763fcd2ab8001994a5fdebf08d58706c553))


### Features

* add new delivery date for delayed orded in production planning bc ([b14b551](https://github.com/atedeg/mdm/commit/b14b551de76f7a01f27da18161f3cd7eee728691))
* bc actions definition ([984a7ba](https://github.com/atedeg/mdm/commit/984a7ba6e6813d73e7285e4d6c4af9e756e645b2))
* bc events definition ([1ae213e](https://github.com/atedeg/mdm/commit/1ae213e4af772da429d072941a7844c7838a0a12))
* bc types definition ([fbdcd83](https://github.com/atedeg/mdm/commit/fbdcd837a169d53e414810b74003d6672d0118b4))
* implement bc actions ([86c6e86](https://github.com/atedeg/mdm/commit/86c6e869432d3def47e7ff6f0a3bbbb63e4c3f5f))

# [1.0.0-beta.5](https://github.com/atedeg/mdm/compare/v1.0.0-beta.4...v1.0.0-beta.5) (2022-08-03)


### Features

* add restocking bc ([36af124](https://github.com/atedeg/mdm/commit/36af124611279107621dca88931483f72eb8ec81))

# [1.0.0-beta.4](https://github.com/atedeg/mdm/compare/v1.0.0-beta.3...v1.0.0-beta.4) (2022-08-02)


### Bug Fixes

* fix ubidoc table ([6b8b0f9](https://github.com/atedeg/mdm/commit/6b8b0f9b152f5c526163116ddb0cfc551e091e2c))
* relationships between restocking and production ([04c182b](https://github.com/atedeg/mdm/commit/04c182b7bc2461d1a5908f82ddf3fcc4150c0a74))

# [1.0.0-beta.3](https://github.com/atedeg/mdm/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2022-08-02)


### Features

* add domain actions and events, closes [#77](https://github.com/atedeg/mdm/issues/77) ([5d50f12](https://github.com/atedeg/mdm/commit/5d50f1221cb6fe5348a711cba2d5d3b0edb81c66))
* add first draft implementation of one of the actions ([474ce44](https://github.com/atedeg/mdm/commit/474ce44f9d7c849307775cd233fe22bc55fc5e81))
* add ubiquitous language,closes [#75](https://github.com/atedeg/mdm/issues/75) ([6cfe906](https://github.com/atedeg/mdm/commit/6cfe906cd70361719c9c24dabc676157b2b4e976))

# [1.0.0-beta.2](https://github.com/atedeg/mdm/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2022-08-01)


### Bug Fixes

* add type annotations to silence wartremover ([0f2e14c](https://github.com/atedeg/mdm/commit/0f2e14c94e479848e2f83052d48145db449a353c))
* **products:** change macro to generate list of weights per cheese type ([038da8b](https://github.com/atedeg/mdm/commit/038da8b78adf337c0bc34ad06bb224abceaed0a7))
* **utils:** move givens from outside compatnion objects and add Distance type class ([64cde37](https://github.com/atedeg/mdm/commit/64cde377efc26a844ce93899390df5939f0ec9f6))


### Features

* add new batch event ([9c0f30f](https://github.com/atedeg/mdm/commit/9c0f30fed2e8192ce96b1aa86f082c5cdf0c6dfa))
* add stocking bounded context ([028ee56](https://github.com/atedeg/mdm/commit/028ee56eff798fa7a6f63eabdb19e77c7ace4b79))
* implement stocking actions ([7cbe9c0](https://github.com/atedeg/mdm/commit/7cbe9c0fa2a6dc7b2ecdfbba9a32d16bdcb17695))
* **stocking:** integrate stocking bounded context with shared kernel ([099bc05](https://github.com/atedeg/mdm/commit/099bc05fd440dc3c582e1ed849076b57c6ca70a9))

# 1.0.0-beta.1 (2022-08-01)


### Features

* complete domain modelling ([956703d](https://github.com/atedeg/mdm/commit/956703d9a4144d5e95dec5264ef4e179f0ee4626))
* complete quintals of milk estimation ([34aa275](https://github.com/atedeg/mdm/commit/34aa275fb14bba2a3264679d4f7b72f67e592fbb))
* define first batch of ubiquitous language ([fa83eee](https://github.com/atedeg/mdm/commit/fa83eeeec89b037a7b3c20ef0e71828bd2039140))
* define incoming and outgoing events ([a2e0174](https://github.com/atedeg/mdm/commit/a2e01748f7a94c7d2117a44e10c7ca048f3690bb))
* definition of domain events for milk-planning ([242a875](https://github.com/atedeg/mdm/commit/242a875a1684d22ace87ba51d04b4a5b1589c790))
* first basic implementation for estimate quintals of milk ([697e87d](https://github.com/atedeg/mdm/commit/697e87df07da321d200e29d499396b362743f9cf))
* first definition of domain actions ([fcdcbbf](https://github.com/atedeg/mdm/commit/fcdcbbf16818d8103e595fd9f44476f77e2fa0be))
* implement logic for calculate milk needed for products ([d914fe7](https://github.com/atedeg/mdm/commit/d914fe7bafa9211be938c4bd61d064bf8f9774e0))
