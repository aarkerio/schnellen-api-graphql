(ns lacinia-ped.service
  (:require [clojure.data.json :as json]
            [io.pedestal.log :as log]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.route.definition.table :refer [table-routes]]
            [io.pedestal.http.body-params :as body-params]
            [lacinia-ped.db.core :as db]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))
(defn prro-page
  [request]
  (let [nm    (get-in request [:query-params :team])
        post  (db/get-post {:id 9})
        _     (log/info :msg (str ">>> POST TITLE >>>>> "  (:title post)))]
    (ring-resp/response (format "Cool Clojure %s - served from %s and team %s ans post %s"
                                (clojure-version)
                                (route/url-for ::prro-page)
                                nm
                                (post :title)))))

(defn home-page
  [request]
  (ring-resp/response "Hello Stupid World!"))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

(def echo
  {:name ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (:ok request)]
              (assoc context :response response)))})

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [ctx]
     (let [accepted         (get-in ctx [:request :accept :field] "text/plain")
           response         (get ctx :response)
           body             (get response :body)
           _                (log/info :msg (str ">>> RESPONSE >>>>> " response))
           coerced-body     (case accepted
                              "text/html"        body
                              "text/plain"       body
                              "application/edn"  (pr-str body)
                              "application/json" (json/write-str body))
           updated-response (assoc response
                                   :headers {"Content-Type" accepted}
                                   :body    coerced-body)]
       (assoc ctx :response updated-response)))})

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors coerce-body `home-page)]
              ["/about" :get (conj common-interceptors `about-page)]
              ["/prro" :get (conj common-interceptors `prro-page)]
              ["/echo"  :get echo]})

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by lacinia-ped.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

 (defn named-route
  "Finds a route by name"
  [route-name]
  (->> routes
       table-routes
       (filter #(= route-name (:route-name %)))
       first))

(defn print-route
  "Prints a route and its interceptors"
  [rname]
  (letfn [(joined-by
            [s coll]
            (apply str (interpose s coll)))

          (repeat-str
            [s n]
            (apply str (repeat n s)))

          (interceptor-info
            [i]
            (let [iname  (or (:name i) "<handler>")
                  stages (joined-by
                          ","
                          (keys
                           (filter
                            (comp (complement nil?) val)
                            (dissoc i :name))))]
              (str iname " (" stages ")")))]
    (when-let [rte (named-route rname)]
      (let [{:keys [path method route-name interceptors]} rte
            name-line                                     (str "[" method " " path " " route-name "]")]
        (joined-by
         "\n"
         (into [name-line (repeat-str "-" (count name-line))]
               (map interceptor-info interceptors)))))))

(defn recognize-route
  "Verifies the requested HTTP verb and path are recognized by the router."
  [verb path]
  (route/try-routing-for (table-routes routes) :prefix-tree path verb))
