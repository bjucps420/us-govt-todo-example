export const state = () => ({
  currentUser: null,
});

export const mutations = {
  setUser(state, user) {
    state.currentUser = user.user;
  },
}
