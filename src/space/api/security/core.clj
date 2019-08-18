(ns space.api.security.core
  (:require [clj-time.core :as time]
            [buddy.auth.backends.token :as jws]
            [buddy.sign.jwt :as jwt]
            [buddy.core.nonce :as nonce]))

;; Secret nonce for the session
(def secret (nonce/random-bytes 32))

;; Backend for authenticating users
(def auth-backend (jws/jws-backend {:secret secret :options {:alg :hs512}}))

(defn make-token
  "Creates a token for the given user"
  [user-id]
  (let [claims 
          { :usr user-id
            :exp (time/plus (time/now) (time/seconds 3600))}]
    (jwt/sign claims secret {:alg :hs512})))
