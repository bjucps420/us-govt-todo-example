export const state = () => ({
  currentUser: {roles: []},
});

export const mutations = {
  setUser(state, user) {
    state.currentUser = user.user;
  },
}
