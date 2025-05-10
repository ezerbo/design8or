#!/bin/bash

web-push send-notification \
  --endpoint="https://fcm.googleapis.com/fcm/send/euk_xuj3yc0:APA91bGxe51K6d9ISyYnBDsb8lAmO2LsVGgXogF5kgxyn4wxPehuqlZuwtZNf_tKKWMheskVFy79IPqeWTsE1Y10jRhbEMeKpFBVbMJ_u2M1XlHtWZULOdzkxOTxbsQCSxQ_TX5Y2wMV" \
  --key="BAG6aC0Y_R-Lp3qCUgDI9_YFD_kFfk9hw6OW_vMgGzdUciP0qJuq4EgvIvGLsiVMJiKlwAeaeg9xhyPwbMskuT8" \
  --auth="04pI3N9c5uS3O9yxoev0WA" \
  --vapid-subject="mailto:zerboedouard@gmail.com" \
  --vapid-pubkey="BOsT9tYV7LuR21EAs5AZ6qCwb9zsD8RRApInBOwVJwD5EOzYfoyPfbP0M0CYXI14lscch43JSiPO45b-B_n-dJo" \
  --vapid-pvtkey="7W5kxKIZUZ8yysvSUbW2mtNPou9HmpajsES2JRmAZO0" \
  --payload='{"title":"Designator Event","body":"You have been designated."}'