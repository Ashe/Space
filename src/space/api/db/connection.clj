(ns space.api.db.connection)

;; Atom for connecting to the database
(def spec (atom 
  { :dbtype "postgresql"
    :dbname "space"
    :user "space"
    :password "nebula"}))

(defn set-host
  "Change the host in db-spec"
  [host]
  (println "- Setting host to: " host)
  (swap! spec #(assoc % :host host)))

(defn set-port
  "Change the port in db-spec"
  [port]
  (println "- Setting port to: " port)
  (swap! spec #(assoc % :port port)))
