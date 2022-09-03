<template>
  <div>
    <v-row v-if="$auth.loggedIn" justify="center" class="my-md-5" :no-gutters="$vuetify.breakpoint.mdAndUp ? false : true">
      <v-col cols="12" md="5" lg="4">
        <v-card
          flat
          width="100%"
          :class="$vuetify.breakpoint.mdAndUp ? 'rounded-lg' : null"
          :tile="$vuetify.breakpoint.mdAndDown"
        >
          <div class="secondary pa-6">
            <h2 class="font-weight-light white--text mb-n2">User Settings</h2>
            <span class="text-caption font-weight-light white--text">Update your information</span>
          </div>

          <div class="pa-8">
            <!-- ~~~~~~ ~~~~~~ USERNAME ~~~~~~ ~~~~~~ -->
            <h2>Username</h2>
            <v-form ref="usernameForm">
              <v-text-field
                v-model="username"
                :label="username ? null : 'No username defined'"
                disabled
                prepend-icon="mdi-account"
              ></v-text-field>
            </v-form>

            <!-- ~~~~~~ ~~~~~~ EMAIL ~~~~~~ ~~~~~~ -->
            <h2>Update Email</h2>
            <v-form ref="emailForm">
              <v-text-field
                v-model="email"
                :rules="emailRules"
                label="Email"
                prepend-icon="mdi-at"
              ></v-text-field>
            </v-form>
            <v-btn color="primary" class="mt-4 rounded-pill" depressed @click="updateEmail">
              Update Email
              <v-icon right dark>mdi-at</v-icon>
            </v-btn>
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" md="5" lg="4">
        <v-card
          flat
          width="100%"
          :class="$vuetify.breakpoint.mdAndUp ? 'rounded-lg' : null"
          :tile="$vuetify.breakpoint.mdAndDown"
        >
          <div class="secondary pa-6">
            <h2 class="font-weight-light white--text mb-n2">Security Settings</h2>
            <span class="text-caption font-weight-light white--text">Secure your information</span>
          </div>

          <!-- ~~~~~~ ~~~~~~ PASSWORD ~~~~~~ ~~~~~~ -->
          <div class="pa-8">
            <h2>Update Password</h2>
            <v-form ref="passwordForm">
              <v-row>
                <v-col cols="12">
                  Please note that passwords must be at least 12 characters in length and contain at least 1 uppercase letter (i.e. ABC), at least 1 lowercase letter (i.e abc), and at least 1 number (i.e. 123).
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    v-model="currentPassword"
                    :rules="currentPasswordRules"
                    dense
                    type="password"
                    label="Current Password"
                    prepend-icon="mdi-lock"
                  ></v-text-field>
                  <v-text-field
                    v-model="newPassword"
                    :rules="newPasswordRules"
                    dense
                    type="password"
                    label="New Password"
                    prepend-icon="mdi-lock"
                  ></v-text-field>
                  <v-text-field
                    v-model="confirmPassword"
                    :rules="confirmPasswordRules"
                    dense
                    type="password"
                    label="Confirm Password"
                    prepend-icon="mdi-lock"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-form>
            <v-btn color="primary" class="mt-4 mb-14 rounded-pill" depressed @click="updatePassword">
              Update Password <v-icon right dark>mdi-lock</v-icon>
            </v-btn>


            <!-- ~~~~~~ ~~~~~~ 2FA ~~~~~~ ~~~~~~ -->
            <h2>Two Factor Authentication</h2>
            <p>Two-factor authentication (<strong>2FA</strong>) is an extra layer of security used when logging into websites or apps. With 2FA, you have to log in with your username and password and provide another form of authentication that only you know or have access to.</p>
            <v-btn
              v-if="!twoFactorEnabled"
              color="primary"
              depressed
              class="rounded-pill"
              @click="enableTwoFactorAuthentication"
              >
              Enable 2FA
              <v-icon right dark>mdi-cellphone-lock</v-icon>
            </v-btn>
            <v-btn v-else color="primary" class="rounded-pill" depressed @click="disableTwoFactorAuthentication">
              Disable 2FA
              <v-icon right dark>
                mdi-cellphone-remove
              </v-icon>
            </v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- ~~~~~~ ~~~~~~ DIALOGS ~~~~~~ ~~~~~~ -->
    <v-dialog v-model="enableDialog" persistent max-width="600px">
      <v-card>
        <v-toolbar flat dense color="secondary">
          <v-toolbar-title class="white--text">Enable Two Factor Authentication</v-toolbar-title>
          <v-spacer />
          <v-btn
            dark
            icon
            @click="cancelEnableTwoFactorAuthentication"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <v-card-text>
          <p class="my-3">
            Use an application on your mobile device to scan the QR Code below to receive a two-factor authentication code. Enter the code to confirm that 2FA was successfully enabled on your mobile device. We recommend using cloud-based TOTP (Time-based One Time Password) apps such as:<br /> <v-chip pill href="https://support.1password.com/one-time-passwords/">1Password</v-chip>
            <v-chip pill href="https://authy.com/guides/github/">Authy</v-chip>
            <v-chip pill href="https://support.logmeininc.com/lastpass/help/lastpass-authenticator-lp030014">LastPass Authenticator</v-chip>
            <v-chip pill href="https://www.microsoft.com/en-us/account/authenticator/">Microsoft Authenticator</v-chip>
          </p>
          <v-form ref="enableForm">
            <v-row>
              <v-col cols="12" sm="5">
                <canvas id="canvas" class="centered-canvas"></canvas>
              </v-col>
              <v-col cols="12" sm="6" align-self="center">
                <v-text-field
                  v-model="code"
                  :rules="codeRules"
                  label="2FA Code"
                  prepend-icon="mdi-clock"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="secondary"
            depressed
            class="rounded-pill"
            @click="completeEnableTwoFactorAuthentication"
          >
            Continue
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog
      v-model="disableDialog"
      persistent
      max-width="600px"
    >
      <v-card>
        <v-toolbar flat dense color="secondary">
          <v-toolbar-title class="white--text">Disable Two Factor Authentication</v-toolbar-title>
          <v-spacer />
          <v-btn
            dark
            icon
            @click="cancelEnableTwoFactorAuthentication"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <v-card-title>
          <span class="text-h5">Disable Two Factor Authentication</span>
        </v-card-title>
        <v-card-text>
          <p>Use your 2FA app to receive a two-factor authentication code. Enter the code below to turn off 2FA.
          </p>
          <v-form ref="disableForm">
            <v-text-field
              v-model="code"
              :rules="codeRules"
              label="2FA Code"
              prepend-icon="mdi-clock"
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="secondary"
            depressed
            class="rounded-pill"
            @click="completeDisableTwoFactorAuthentication"
          >
            Continue
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapState } from "vuex";

const QRCode = require('qrcode')

export default {
  name: 'AccountPage',
  data () {
    return {
      email: '',
      code: '',
      enableDialog: false,
      disableDialog: false,
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
      secret: '',
      secretBase32: '',
      emailRules: [
        v => !!v || 'Email is required',
        v => (v || '').match(/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/) !== null || 'Email must be a valid email',
      ],
      codeRules: [
        v => !!v || 'Code is required',
      ],
      currentPasswordRules: [
        v => !!v || 'Current Password is required',
      ],
      newPasswordRules: [
        v => !!v || 'New Password is required',
        v => v.length >= 12 || 'New Password must be a least 12 characters long',
        v => v.replace(/[^a-z]/g, '').length >= 1 || 'New Password must contain at least 1 lower case letter',
        v => v.replace(/[^A-Z]/g, '').length >= 1 || 'New Password must contain at least 1 upper case letter',
        v => v.replace(/[^0-9]/g, '').length >= 1 || 'New Password must contain at least 1 number',
      ],
      confirmPasswordRules: [
        v => !!v || 'Confirm Password is required',
        v => v === this.newPassword || 'Password and Confirm Password must match',
      ],
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.user.currentUser,
    }),
    username() {
      if(this.currentUser) {
        return this.currentUser.username;
      }
      return '';
    },
    twoFactorEnabled() {
      if(this.currentUser) {
        return this.currentUser.twoFactorEnabled;
      }
      return false;
    },
  },
  async mounted() {
    const user = await this.$userService.getCurrent();
    this.email = user.user.email;
  },
  methods: {
    async enableTwoFactorAuthentication() {
      const response = await this.$userService.getSecret();
      if(response.success) {
        this.secret = response.response.secret;
        this.secretBase32 = response.response.secretBase32Encoded;
        this.enableDialog = true;
        this.$nextTick(() => {
          const canvas = document.getElementById('canvas');
          QRCode.toCanvas(canvas, 'otpauth://totp/sichra?secret=' + this.secretBase32 + '&issuer=fusion-auth');
        });
      }
    },
    async completeEnableTwoFactorAuthentication() {
      if(this.$refs.enableForm.validate()) {
        const response = await this.$userService.toggleTwoFactor(true, this.secret, this.secretBase32, this.code);
        if(response.success && response.response) {
          const user = {...this.currentUser};
          user.twoFactorEnabled =  true;
          this.$store.commit('user/setUser', { user });
          this.enableDialog = false;
          this.code = '';
        }
      }
    },
    cancelEnableTwoFactorAuthentication() {
      this.enableDialog = false;
    },
    disableTwoFactorAuthentication() {
      this.disableDialog = true;
    },
    async completeDisableTwoFactorAuthentication() {
      if(this.$refs.disableForm.validate()) {
        const response = await this.$userService.toggleTwoFactor(false, null, null, this.code);
        if(response.success && response.response) {
          const user = {...this.currentUser};
          user.twoFactorEnabled =  false;
          this.$store.commit('user/setUser', { user });
          this.disableDialog = false;
          this.code = '';
        }
      }
    },
    async updateEmail() {
      if(this.$refs.usernameForm.validate()) {
        const response = await this.$userService.changeEmail(this.email);
        if(response.success && response.response) {
          this.$toast.success('Email updated.', {
            duration: 3000
          });
        } else {
          this.$toast.error('Email update failed.', {
            duration: 3000
          });
        }
      }
    },
    async updatePassword() {
      if(this.$refs.passwordForm.validate()) {
        const response = await this.$userService.changePassword(this.currentPassword, this.newPassword);
        if(response.success && response.response) {
          this.$toast.success('Password updated.', {
            duration: 3000
          });
        } else {
          this.$toast.error('Password update failed. Check your login credentials and try again.', {
            duration: 3000
          });
        }
      }
    }
  },
}
</script>

<style scoped>
.centered-canvas {
  margin: auto;
  display: block;
}
</style>
