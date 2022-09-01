<template>
  <div>
    <v-row justify="center" align="center" :style="$vuetify.breakpoint.smAndUp ? 'height: 100vh' : null" no-gutters>
      <v-card
        flat
        :class="$vuetify.breakpoint.lgAndUp ? 'rounded-lg' : null"
        :tile="$vuetify.breakpoint.mdAndDown"
        :style="{width: $vuetify.breakpoint.mdAndDown ? '100vw' : '50vw'}"
        :color="$vuetify.breakpoint.smAndUp ? 'grey lighten-4' : ''"
      >
        <v-card-text class="pa-9 pa-sm-15">
          <div v-if="page == LOGIN">
            <v-form ref="userPasswordForm">
              <v-row align="center">
                <v-col cols="12">
                  <v-row no-gutters>
                    <v-col cols="12">
                      <v-text-field
                        v-model="username"
                        :rules="usernameRules"
                        label="Username"
                        required
                        prepend-icon="mdi-account"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12">
                      <v-text-field
                        v-model="password"
                        :rules="passwordRules"
                        label="Password"
                        type="password"
                        required
                        prepend-icon="mdi-lock"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" class="mt-4">
                      <v-row>
                        <v-btn
                          :block="$vuetify.breakpoint.smAndUp ? false : true"
                          color="secondary lighten-2"
                          class="my-4 rounded-pill"
                          depressed
                          @click="forgotPassword"
                        >
                          Forgot Password
                        </v-btn>
                        <v-spacer />
                        <v-btn
                          :block="$vuetify.breakpoint.smAndUp ? false : true"
                          color="primary"
                          class="my-4 px-6 rounded-pill"
                          depressed
                          @click="login"
                        >
                          Login
                        </v-btn>
                      </v-row>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </v-form>
          </div>

          <div v-if="page == FORGOT_PASSWORD">
            <v-form ref="forgotPasswordForm">
              <v-row>
                <v-col cols="12">
                  Please enter your username or email in order to receive an email with a link to reset your password.
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="username"
                    :rules="usernameRules"
                    label="Email or Username"
                    required
                    prepend-icon="mdi-at"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-btn
                  color="secondary"
                  class="my-4 rounded-pill"
                  depressed
                  @click="back"
                >
                  Back
                </v-btn>
                <v-spacer/>
                <v-btn
                  color="primary"
                  class="my-4 rounded-pill"
                  depressed
                  @click="sendForgotPasswordEmail"
                >
                  Send Email
                </v-btn>
              </v-row>
            </v-form>
          </div>

          <div v-if="page == FORGOT_PASSWORD_COMPLETE || page == PASSWORD_CHANGE">
            <v-form ref="completeChangePasswordForm">
              <v-row>
                <v-col cols="12">
                  Enter a new password below. Please note that passwords must be at least 12 characters in length and contain at least 1 uppercase letter (i.e. ABC), at least 1 lowercase letter (i.e abc), and at least 1 number (i.e. 123).
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="newPassword"
                    :rules="newPasswordRules"
                    label="New Password"
                    type="password"
                    required
                    prepend-icon="mdi-lock"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="confirmPassword"
                    :rules="confirmPasswordRules"
                    label="Confirm Password"
                    type="password"
                    required
                    prepend-icon="mdi-lock"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-spacer/>
                <v-btn
                  color="primary"
                  class="my-4 rounded-pill"
                  depressed
                  @click="login"
                >
                  Change Password
                </v-btn>
              </v-row>
            </v-form>
          </div>

          <div v-if="page == TWO_FACTOR_CODE">
            <v-form ref="twoFactorForm">
              <v-row>
                <v-col cols="12">
                  Please enter the two factor authentication code from your authenticator application.  If you no longer have access to your authenticator application, please contact our helpdesk for assistance.
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="twoFactorCode"
                    :rules="twoFactorCodeRules"
                    label="2FA Code"
                    required
                    prepend-icon="mdi-lock"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-spacer/>
                <v-btn
                  color="primary"
                  class="my-4 rounded-pill"
                  depressed
                  @click="login"
                >
                  Login
                </v-btn>
              </v-row>
            </v-form>
          </div>
        </v-card-text>
      </v-card>
    </v-row>
    <v-dialog
      v-model="showStartForgotPasswordDialog"
      persistent
    >
      <v-card class="rounded-lg">
        <v-toolbar flat color="secondary">
          <v-toolbar-title class="font-weight-light white--text">Forgot Password</v-toolbar-title>
          <v-spacer />
          <v-btn color="white" icon @click="showStartForgotPasswordDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <div class="pa-5">
          If there is an account associated with this username / email, you will receive an email with further instructions.  Contact support for additional assistance or if you do not receive an email.
        </div>
      </v-card>
    </v-dialog>
    <v-dialog
      v-model="showLoginFailedDialog"
      persistent
      max-width="900"
    >
      <v-card class="rounded-lg mx-auto">
        <v-toolbar flat color="error">
          <v-toolbar-title class="font-weight-light white--text">Login Failed</v-toolbar-title>
          <v-spacer />
          <v-btn color="white" icon @click="showLoginFailedDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <div class="pa-5 pa-sm-10 text-center">
          Your login attempt has failed due to an incorrect password or incorrect 2FA code. Please try again.
        </div>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      LOGIN: 0,
      FORGOT_PASSWORD: 1,
      PASSWORD_CHANGE: 2,
      FORGOT_PASSWORD_COMPLETE: 3,
      TWO_FACTOR_CODE: 4,

      page: 0,
      username: null,
      password: null,
      newPassword: null,
      confirmPassword: null,
      twoFactorCode: null,
      twoFactorRequired: false,
      passwordChangeRequired: false,
      passwordChangeCode: null,

      showStartForgotPasswordDialog: false,
      showLoginFailedDialog: false,

      usernameRules: [
        v => !!v || 'Username is required',
      ],
      passwordRules: [
        v => !!v || 'Password is required',
      ],
      twoFactorCodeRules: [
        v => !!v || 'Two Factor Code is required',
      ],
      newPasswordRules: [
        v => !!v || 'Password is required',
        v => (v || '').length >= 12 || 'Password must be a least 12 characters long',
        v => (v || '').replace(/[^a-z]/g, '').length >= 1 || 'Password must contain at least 1 lower case letter',
        v => (v || '').replace(/[^A-Z]/g, '').length >= 1 || 'Password must contain at least 1 upper case letter',
        v => (v || '').replace(/[^0-9]/g, '').length >= 1 || 'Password must contain at least 1 number',
      ],
      confirmPasswordRules: [
        v => !!v || 'Confirm Password is required',
        v => v === this.newPassword || 'Password and Confirm Password must match',
      ],
    };
  },
  created() {
    if(this.$route.query.passwordChangeCode) {
      this.page = this.FORGOT_PASSWORD_COMPLETE;
      this.passwordChangeCode = this.$route.query.passwordChangeCode;
    } else {
      this.page = this.LOGIN;
    }
  },
  methods: {
    back() {
      this.page = this.LOGIN;
    },
    forgotPassword() {
      this.page = this.FORGOT_PASSWORD;
    },
    async sendForgotPasswordEmail() {
      if(this.$refs.forgotPasswordForm.validate()) {
        await this.$authService.startForgotPassword(this.username);
        this.showStartForgotPasswordDialog = true;
        this.page = this.LOGIN;
      }
    },
    async login() {
      if((this.$refs.userPasswordForm && this.$refs.userPasswordForm.validate())
        || (!this.twoFactorRequired || (this.$refs.twoFactorForm && this.$refs.twoFactorForm.validate()))
        || (!this.passwordChangeRequired || (this.$refs.completeChangePasswordForm && this.$refs.completeChangePasswordForm.validate()))) {
        let response = await this.$authService.login({
          username: this.username,
          password: this.password,
          newPassword: this.newPassword,
          twoFactorCode: this.twoFactorCode,
          forgotPasswordCode: this.passwordChangeCode
        });
        if(response.success) {
          response = response.response;
          if (response.requiresTwoFactorCode) {
            this.page = this.TWO_FACTOR_CODE;
            this.twoFactorRequired = true;
          } else if (response.requiresPasswordChange) {
            this.page = this.FORGOT_PASSWORD_COMPLETE;
            this.passwordChangeRequired = true;
          } else {
            await this.$auth.loginWith('local', { data: {
              username: this.username,
              password: this.password,
              newPassword: this.newPassword,
              twoFactorCode: this.twoFactorCode,
              forgotPasswordCode: this.passwordChangeCode
            }});
            this.$router.push({path: "/"});
          }
        } else {
          this.showLoginFailedDialog = true;
          this.password = null;
          this.twoFactorCode = null;
          this.newPassword = null;
          this.confirmPassword = null;
          this.passwordChangeCode = null;
          this.page = this.LOGIN;
        }
      }
    }
  }
}
</script>