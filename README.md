# clj-cron-parse

A Clojure library for using cron expressions.

[![CI](https://github.com/treasuryprime/com.treasuryprime.clj-cron-parse/actions/workflows/ci.yaml/badge.svg)](https://github.com/treasuryprime/com.treasuryprime.clj-cron-parse/actions/workflows/ci.yaml)

The library mainly consists of one function, `next-date`, which takes a cron
expression and a from date and returns the next `org.joda.time.DateTime` to
occur for that cron expression.

## Usage

This library is published to [GitHub Packages](https://github.com/treasuryprime/com.treasuryprime.clj-cron-parse/packages)
(not Clojars). Consumers must authenticate to GitHub Packages.

### deps.edn

```clojure
{:deps {com.treasuryprime/clj-cron-parse {:mvn/version "1.3.0"}}

 :mvn/repos
 {"github-treasuryprime"
  {:url "https://maven.pkg.github.com/treasuryprime/com.treasuryprime.clj-cron-parse"}}}
```

### Authentication

The GitHub Packages Maven repo requires a GitHub token with `read:packages`.
Add credentials to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github-treasuryprime</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PAT</password>
    </server>
  </servers>
</settings>
```

The `<id>` must match the key used under `:mvn/repos`.

### Example

```clojure
(require '[clj-time.core :as t]
         '[com.treasuryprime.clj-cron-parse.core :refer [next-date]])

(def now (t/date-time 2015 01 01 12 00 00 000))

(def next-every-second
  (next-date now "1 * * * * *"))

(def next-every-year
  (next-date now "@yearly"))
```

## Development

```bash
clojure -M:test                 # run unit tests (Kaocha)
clojure -M:test/watch           # run tests in watch mode
clojure -M:cljfmt check         # lint formatting
clojure -M:cljfmt fix           # auto-fix formatting
clojure -T:build clean          # clean target/
clojure -T:build jar            # build target/clj-cron-parse-<ver>.jar
clojure -T:build install        # install to ~/.m2 for local consumers
```

## Releasing

Releases are cut manually from a maintainer's laptop.

1. Bump `version` in `build.clj` (e.g. `"1.3.0"` -> `"1.3.1"`).
2. Commit, open PR, merge to `main`.
3. Tag the merge commit: `git tag v1.3.1 && git push origin v1.3.1`.
4. Deploy:
   ```bash
   export CLOJARS_USERNAME=<your-github-username>
   export CLOJARS_PASSWORD=<github-pat-with-write:packages>
   clojure -T:build deploy
   ```
   (`slipset/deps-deploy` reads `CLOJARS_*` env vars; our `build.clj` points them at GitHub Packages, not Clojars.)

## License

Copyright © 2015 David Smith

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
