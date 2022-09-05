import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { store } from './store/createStore'
import ElementPlus from 'element-plus';
import 'element-plus/lib/theme-chalk/index.css';


router.beforeEach((to, from, next) => {
    //��������ҳ��ʱ �ض�������¼ҳ��
    const publicPages = ['/login','/Register'];
    const authRequired = !publicPages.includes(to.path);

    var loggedIn = store.state.loggedIn

    if (authRequired && !loggedIn) {
      return next('/login');
    }

    next();
  })



createApp(App)
.use(ElementPlus)
.use(store).use(router).mount('#app')

