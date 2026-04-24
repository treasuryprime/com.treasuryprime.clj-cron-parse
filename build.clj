(ns build
  "tools.build entry points for com.treasuryprime/clj-cron-parse.

   Usage:
     clojure -T:build clean
     clojure -T:build jar
     clojure -T:build install
     clojure -T:build deploy     ; requires CLOJARS_USERNAME + CLOJARS_PASSWORD env vars"
  (:require
   [clojure.tools.build.api :as b]
   [deps-deploy.deps-deploy :as dd]))

(def lib 'com.treasuryprime/clj-cron-parse)
(def version "1.3.0")
(def class-dir "target/classes")
(def basis (delay (b/create-basis {:project "deps.edn"})))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(def scm
  {:url                 "https://github.com/treasuryprime/com.treasuryprime.clj-cron-parse"
   :connection          "scm:git:git://github.com/treasuryprime/com.treasuryprime.clj-cron-parse.git"
   :developerConnection "scm:git:ssh://git@github.com/treasuryprime/com.treasuryprime.clj-cron-parse.git"
   :tag                 (or (System/getenv "GITHUB_SHA") version)})

(def pom-data
  [[:description "A Clojure library for using cron expressions"]
   [:url "https://github.com/treasuryprime/com.treasuryprime.clj-cron-parse"]
   [:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "http://www.eclipse.org/legal/epl-v10.html"]]]])

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (clean nil)
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :basis     @basis
                :src-dirs  ["src"]
                :scm       scm
                :pom-data  pom-data})
  (b/copy-dir {:src-dirs ["src"] :target-dir class-dir})
  (b/jar {:class-dir class-dir :jar-file jar-file})
  (println "Wrote" jar-file))

(defn install [_]
  (jar nil)
  (dd/deploy {:installer :local
              :artifact  jar-file
              :pom-file  (b/pom-path {:lib lib :class-dir class-dir})}))

(defn deploy
  "Deploy to GitHub Packages. Env vars required:
     CLOJARS_USERNAME -> GitHub username
     CLOJARS_PASSWORD -> GitHub PAT with `write:packages`
   (slipset/deps-deploy reads these unconditionally and forwards as basic-auth;
   they are not actually sent to Clojars - our :repository targets GitHub.)"
  [_]
  (jar nil)
  (dd/deploy {:installer      :remote
              :artifact       jar-file
              :pom-file       (b/pom-path {:lib lib :class-dir class-dir})
              :sign-releases? false
              :repository     {"github"
                               {:url "https://maven.pkg.github.com/treasuryprime/com.treasuryprime.clj-cron-parse"}}}))
