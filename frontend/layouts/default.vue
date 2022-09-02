<template>
  <v-app dark>
    <v-app-bar fixed app elevate-on-scroll>
      <v-btn text class="mr-2" color="secondary" @click="goHome">
        <img v-if="$vuetify.theme.dark" height="36" src="/logo-dark.png" alt="Logo" class="logo ml-3">
        <img v-else height="36" src="/logo.png" alt="Logo" class="logo ml-3">
        <span>Presidential Todos</span>
      </v-btn>

      <v-spacer />

      <v-toolbar-items>
        <v-btn text class="mr-2" color="secondary" @click="toggleTheme">
          <v-icon small left>mdi-brightness-6</v-icon>Theme
        </v-btn>
        <v-btn v-if="!!currentUser.fusionAuthUserId" text class="mr-2" color="secondary" @click="profile">
          <v-icon small left color="primary">mdi-account-circle</v-icon>Account
        </v-btn>
        <v-btn v-if="!!currentUser.fusionAuthUserId" text color="secondary" @click="logout">
          <v-icon small left color="primary">mdi-logout-variant</v-icon>Logout
        </v-btn>
        <v-btn v-if="!!!currentUser.fusionAuthUserId" text color="secondary" @click="login">
          <v-icon small left color="primary">mdi-login-variant</v-icon>Login
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <v-main :class="!!currentUser ? 'main-bg' : null">
      <Nuxt />
    </v-main>
  </v-app>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'DefaultLayout',
  data() {
    return {
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.user.currentUser,
    }),
  },
  async mounted() {
    const user = await this.$userService.getCurrent();
    if(user) {
      this.$store.commit('user/setUser', { user: user.user });
    }
    const theme = localStorage.getItem("useDarkTheme");
    if (theme) {
      if (theme === "true") {
        this.$vuetify.theme.dark = true;
      } else {
        this.$vuetify.theme.dark = false;
      }
    } else {
      this.$vuetify.theme.dark = false;
    }
  },
  methods: {
    toggleTheme() {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
      localStorage.setItem("useDarkTheme", this.$vuetify.theme.dark.toString())
    },
    goHome() {
      this.$router.push({path: "/"});
    },
    profile() {
      this.$router.push({path: "/account"});
    },
    login() {
      this.$router.push({path: "/login"});
    },
    async logout() {
      this.$auth.logout();
      await this.$authService.logout();
      this.$store.commit('user/setUser', { user: {roles:[]} });
      this.$router.push({path: "/login"});
    },
  }
}
</script>
