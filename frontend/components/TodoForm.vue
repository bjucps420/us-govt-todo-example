<template>
  <v-card flat tile class="pa-5">
    <v-form ref="form">
      <v-row>
        <v-col cols="12">
          <v-text-field
            v-model="todo.title"
            :disabled="disabled"
            :rules="titleRules"
            label="Title"
            counter="255"
            required
            prepend-icon="mdi-format-title"
          ></v-text-field>
        </v-col>
        <v-col cols="12">
          <v-textarea
            v-model="todo.description"
            :disabled="disabled"
            :rules="descriptionRules"
            label="Description"
            counter="2048"
            required
            prepend-icon="mdi-image-text"
          ></v-textarea>
        </v-col>
        <v-col v-if="currentUser.roles.includes('Aid')" cols="12">
          <v-select
            v-model="todo.status"
            :disabled="disabled"
            :rules="statusRules"
            :items="statuses"
            label="Status"
            required
            prepend-icon="mdi-list-status"
          ></v-select>
        </v-col>
        <v-col v-if="types.length > 1" cols="12">
          <v-select
            v-model="todo.type"
            :disabled="disabled"
            :rules="typeRules"
            :items="types"
            label="Type"
            required
            prepend-icon="mdi-format-list-bulleted-type"
          ></v-select>
        </v-col>
      </v-row>
    </v-form>
  </v-card>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'TodoForm',
  props: {
    request: { type: Object, default: () => {} },
    disabled: { type: Boolean, default: false },
  },
  data() {
    return {
      company: {},
      statuses: [],
      types: [],
      titleRules: [
        v => !!v || 'Title is required',
        v => (v || '').length < 255 || 'Title cannot be longer than 255 characters',
      ],
      descriptionRules: [
        v => !!v || 'Description is required',
        v => (v || '').length < 2048 || 'Description cannot be longer than 2048 characters',
      ],
      statusRules: [
        v => !!v || 'Status is required',
      ],
      typeRules: [
        v => !!v || 'Type is required',
      ],
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.user.currentUser,
    }),
  },
  watch: {
    request() {
      this.reload();
    }
  },
  created() {
    this.fetchEnums();
    this.reload();
  },
  methods: {
    reload() {
      this.todo = {
        id: this.request.id || null,
        title: this.request.title || '',
        description: this.request.description || '',
        status: this.request.status || 'Pending',
        type: this.request.type || 'Unclassified',
      };
    },
    async fetchEnums() {
      this.statuses = await this.$enumService.statuses();
      let fetchedTypes = await this.$enumService.types()
      if(!(this.currentUser.roles.includes('Aid') || this.currentUser.roles.includes("Top Secret"))) {
        fetchedTypes = fetchedTypes.filter(x => x !== "Top Secret");
      }
      if(!(this.currentUser.roles.includes('Aid') || this.currentUser.roles.includes("Secret") || this.currentUser.roles.includes("Top Secret"))) {
        fetchedTypes = fetchedTypes.filter(x => x !== "Secret");
      }
      if(!(this.currentUser.roles.includes('Aid') || this.currentUser.roles.includes("Classified") || this.currentUser.roles.includes("Secret") || this.currentUser.roles.includes("Top Secret"))) {
        fetchedTypes = fetchedTypes.filter(x => x !== "Classified");
      }
      this.types = fetchedTypes;
    },
    async submit() {
      if(this.$refs.form.validate()) {
        if(this.todo.id) {
          return await this.$todoService.update(this.todo);
        } else {
          return this.$todoService.create(this.todo);
        }
      }
      return {success: false};
    },
  }
}
</script>

<style scoped>
.menu {
  margin-top: 15px;
}

.card {
  width: 100%;
  margin-top: 2%;
  margin-bottom: 2%;
  border-radius: 5px;
  padding: 10%;
}
</style>
