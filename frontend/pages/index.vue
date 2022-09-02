<template>
  <v-row v-if="!!currentUser.fusionAuthUserId" justify="center" align="center" class="my-lg-5">
    <v-col cols="12" lg="8">
      <v-card width="100%" :class="$vuetify.breakpoint.lgAndUp ? 'rounded-lg' : null" :tile="$vuetify.breakpoint.mdAndDown">
        <div class="secondary pa-6">
          <h2 class="font-weight-light white--text mb-n2">Todos</h2>
          <span class="text-caption font-weight-light white--text">Pending, In-Progress or Done</span>
        </div>

        <div class="pa-8">
          <v-tabs >
            <v-tab href="#pending">
              Pending
            </v-tab>
            <v-tab-item value="pending">
              <v-row align="end" class="pa-4">
                <v-col cols="4">
                  <v-btn v-if="!currentUser.roles.includes('Aid')" color="secondary" depressed @click="createItem">
                    <v-icon left dark>
                      mdi-plus
                    </v-icon>
                    New Todo
                  </v-btn>
                </v-col>
                <v-spacer />
                <v-col cols="8" sm="4">
                  <v-text-field
                    v-model="pendingSearch"
                    append-icon="mdi-magnify"
                    label="Search"
                    outlined
                    hide-details
                    clearable
                    @change="getPending"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-data-table
                :headers="headers"
                :items="pending"
                :options.sync="pendingOptions"
                :server-items-length="totalPending"
                :loading="pendingLoading"
                class="elevation-1"
                @update:options="getPending"
              >
                <template #[`item.actions`]="{ item }">
                  <v-icon class="mr-xl-2" @click="editItem(item)">
                    mdi-eye
                  </v-icon>
                  <v-icon v-if="currentUser.roles.includes('Aid')" color="red darken-2" @click="deleteItem(item)">
                    mdi-delete
                  </v-icon>
                </template>
              </v-data-table>
            </v-tab-item>

            <v-tab href="#in-progress">
              In-Progress
            </v-tab>
            <v-tab-item value="in-progress">
              <v-row justify="end" class="my-5">
                <v-col cols="5" lg="4">
                  <v-text-field
                    v-model="inProgressSearch"
                    append-icon="mdi-magnify"
                    label="Search"
                    outlined
                    hide-details
                    clearable
                    @change="getInProgress"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-data-table
                :headers="headers"
                :items="inProgress"
                :options.sync="inProgressOptions"
                :server-items-length="totalInProgress"
                :loading="inProgressLoading"
                class="elevation-1"
                @update:options="getInProgress"
              >
                <template #[`item.actions`]="{ item }">
                  <v-icon class="mr-xl-2" @click="editItem(item)">
                    mdi-eye
                  </v-icon>
                  <v-icon v-if="currentUser.roles.includes('Aid')" color="red darken-2" @click="deleteItem(item)">
                    mdi-delete
                  </v-icon>
                </template>
              </v-data-table>
            </v-tab-item>

            <v-tab href="#complete">
              Complete
            </v-tab>
            <v-tab-item value="complete">
              <v-row justify="end" class="my-5">
                <v-col cols="5" lg="4">
                  <v-text-field
                    v-model="completeSearch"
                    append-icon="mdi-magnify"
                    label="Search"
                    outlined
                    hide-details
                    clearable
                    @change="getComplete"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-data-table
                :headers="headers"
                :items="complete"
                :options.sync="completeOptions"
                :server-items-length="totalComplete"
                :loading="completeLoading"
                class="elevation-1"
                @update:options="getComplete"
              >
                <template #[`item.actions`]="{ item }">
                  <v-icon class="mr-xl-2" @click="editItem(item)">
                    mdi-eye
                  </v-icon>
                  <v-icon v-if="currentUser.roles.includes('Aid')" color="red darken-2" @click="deleteItem(item)">
                    mdi-delete
                  </v-icon>
                </template>
              </v-data-table>
            </v-tab-item>
          </v-tabs>
        </div>
      </v-card>
    </v-col>

    <v-dialog
      v-model="createDialog"
      persistent
      max-width="400px"
    >
      <v-card class="rounded-lg">
        <v-toolbar flat color="secondary">
          <v-toolbar-title class="font-weight-light white--text">Create Todo</v-toolbar-title>
          <v-spacer />
          <v-btn color="white" icon @click="createDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <TodoForm ref="createRequest" :request="todo" :disabled="false" />

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            class="my-4 mr-4"
            text
            @click="saveTodo"
          >
            Create
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog
      v-model="editDialog"
      persistent
      max-width="400px"
    >
      <v-card class="rounded-lg">
        <v-toolbar flat color="secondary">
          <v-toolbar-title v-if="editDialogViewing" class="font-weight-light white--text">View Todo</v-toolbar-title>
          <v-toolbar-title v-else class="font-weight-light white--text">Edit Todo</v-toolbar-title>
          <v-spacer />
          <v-btn color="white" icon @click="editDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <TodoForm ref="editRequest" :request="todo" :disabled="editDialogViewing" />

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            v-if="editDialogViewing && currentUser.roles.includes('Aid')"
            color="primary"
            class="my-4 mr-4"
            text
            @click="editDialogViewing = false"
          >
            Edit
          </v-btn>
          <v-btn
            v-else
            color="primary"
            class="my-4 mr-4"
            text
            @click="saveTodo"
          >
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog
      v-model="deleteDialog"
      persistent
      max-width="400px"
    >
      <v-card class="rounded-lg">
        <v-toolbar flat color="secondary">
          <v-toolbar-title class="font-weight-light white--text">Delete Todo</v-toolbar-title>
          <v-spacer />
          <v-btn color="white" icon @click="deleteDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <div class="pa-5">
          This action cannot be undone.  Are you sure you wish to continue?
        </div>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            class="my-4 mr-4"
            text
            @click="deleteTodo"
          >
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-overlay :value="loading">
      <v-progress-circular
        indeterminate
        size="64"
      ></v-progress-circular>
    </v-overlay>
  </v-row>
</template>

<script>
import { mapState } from 'vuex';
import TodoForm from '../components/TodoForm';

export default {
  name: 'IndexPage',
  components: {
    TodoForm,
  },
  data () {
    return {
      loading: false,

      todo: {},
      createDialog: false,
      editDialog: false,
      deleteDialog: false,
      editDialogViewing: true,

      pendingSearch: '',
      inProgressSearch: '',
      completeSearch: '',
      totalPending: 0,
      pending: [],
      totalInProgress: 0,
      inProgress: [],
      totalComplete: 0,
      complete: [],
      pendingLoading: true,
      inProgressLoading: true,
      completeLoading: true,
      pendingOptions: {},
      inProgressOptions: {},
      completeOptions: {},
      headers: [
        { text: 'Title', value: 'title' },
        { text: 'Type', value: 'type' },
        { text: 'Status', value: 'status' },
        { text: 'Created By', value: 'createdBy.username' },
        { text: 'Updated By', value: 'updatedBy.username' },
        { text: 'Actions', value: 'actions' },
      ],
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.user.currentUser,
    })
  },
  watch: {
    currentUser() {
      this.refetch();
    }
  },
  methods: {
    createItem() {
      this.todo = {};
      this.createDialog = true;
    },
    editItem(item) {
      this.todo = item;
      this.editDialogViewing = true;
      this.editDialog = true;
    },
    deleteItem(item) {
      this.todo = item;
      this.deleteDialog = true;
    },
    async deleteTodo() {
      this.loading = true;
      this.deleteDialog = false;
      await this.$todoService.delete(this.todo);
      this.refetch();
      this.loading = false;
    },
    async saveTodo() {
      this.loading = true;
      if(this.createDialog) {
        const response = await this.$refs.createRequest.submit();
        if(response.success) {
          this.createDialog = false;
        } else if(response.errorMessage) {
          this.errorMessage = response.errorMessage;
          this.errorDialog = true;
        }
      } else if(this.editDialog) {
        const response = await this.$refs.editRequest.submit();
        if(response.success) {
          this.editDialog = false;
        } else if(response.errorMessage) {
          this.errorMessage = response.errorMessage;
          this.errorDialog = true;
        }
      }
      this.refetch();
      this.loading = false;
    },
    refetch() {
      this.getDataFromApi();
    },
    getDataFromApi() {
      this.getPending();
      this.getInProgress();
      this.getComplete();
    },
    getPending() {
      this.fetchPending().then((data) => {
        this.pending = data.response.items;
        this.totalPending = data.response.total;
        this.pendingLoading = false;
      });
    },
    fetchPending() {
      return new Promise((resolve, reject) => {
        if(this.pendingSearch) {
          this.pendingOptions.search = this.pendingSearch;
        }
        this.$todoService.list('PENDING', this.pendingOptions).then((response) => {
          if(response.status === 200) {
            resolve(response.data);
          }
        });
      })
    },
    getInProgress() {
      this.fetchInProgress().then((data) => {
        this.inProgress = data.response.items;
        this.totalInProgress = data.response.total;
        this.inProgressLoading = false;
      });
    },
    fetchInProgress() {
      return new Promise((resolve, reject) => {
        if(this.inProgressSearch) {
          this.inProgressOptions.search = this.inProgressSearch;
        }
        this.$todoService.list('IN_PROGRESS', this.inProgressOptions).then((response) => {
          if(response.status === 200) {
            resolve(response.data);
          }
        });
      })
    },
    getComplete() {
      this.fetchComplete().then((data) => {
        this.complete = data.response.items;
        this.totalComplete = data.response.total;
        this.completeLoading = false;
      });
    },
    fetchComplete() {
      return new Promise((resolve, reject) => {
        if(this.completeSearch) {
          this.completeOptions.search = this.completeSearch;
        }
        this.$todoService.list('COMPLETE', this.completeOptions).then((response) => {
          if(response.status === 200) {
            resolve(response.data);
          }
        });
      })
    }
  }
}
</script>
