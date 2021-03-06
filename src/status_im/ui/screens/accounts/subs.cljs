(ns status-im.ui.screens.accounts.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [clojure.string :as string]
            [status-im.ui.screens.accounts.db :as db]
            [status-im.utils.ethereum.core :as ethereum]
            [cljs.spec.alpha :as spec]))

(reg-sub :get-current-public-key
  (fn [db]
    (:current-public-key db)))

(reg-sub :get-accounts
  (fn [db]
    (:accounts/accounts db)))

(reg-sub :get-current-account-id
  (fn [db]
    (:accounts/current-account-id db)))

(reg-sub :get-current-account
  :<- [:get-current-account-id]
  :<- [:get-accounts]
  (fn [[account-id accounts]]
    (some-> accounts (get account-id))))

(reg-sub :get-current-account-hex
  :<- [:get-current-account-id]
  (fn [address]
    (ethereum/normalized-address address)))

(reg-sub
  :get-account-creation-next-enabled?
  (fn [{:accounts/keys [create]}]
   (let [{:keys [step password password-confirm name]} create]
     (or (and password (= :enter-password step) (spec/valid? ::db/password password))
         (and password-confirm (= :confirm-password step) (spec/valid? ::db/password password-confirm))
         (and name (= :enter-name step) (not (string/blank? name)))))))