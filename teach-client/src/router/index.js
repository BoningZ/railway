import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login'

const routes = [
  {
    path: '/',
    name: 'Home',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '@/views/Home.vue')
  },

  {
    path: '/login',
    name: 'Login',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: Login
  },

  {
    path: '/DashSample',
    name: 'DashSample',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '@/views/dashboard/DashSample.vue')
  },

  {
    path: '/ChangePassword',
    name: 'ChangePassword',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '@/views/dashboard/ChangePassword.vue')
  },

  {
    path:'/Register',
    name:'Register',
    component:() => import('@/views/Register.vue')
  },
  {
    path: '/PassengerProfile',
    name:'PassengerProfile',
    component:()=>import('@/views/dashboard/PassengerProfile')
  },
  {
    path: '/Booking',
    name:'Booking',
    component:()=>import('@/views/passenger/Booking')
  },
  {
    path: '/EditTravel',
    name:'EditTravel',
    component:()=>import('@/views/passenger/EditTravel')
  },
  {
    path: '/MyTravel',
    name:'MyTravel',
    component:()=>import('@/views/passenger/MyTravel')
  },
  {
    path: '/AlterTicket',
    name:'AlterTicket',
    component:()=>import('@/views/passenger/AlterTicket')
  },
  {
    path: '/SeatTable',
    name:'SeatTable',
    component:()=>import('@/views/trainAdmin/SeatTable')
  },
  {
    path: '/CoachTable',
    name:'CoachTable',
    component:()=>import('@/views/trainAdmin/CoachTable')
  },
  {
    path: '/CoachEdit',
    name:'CoachEdit',
    component:()=>import('@/views/trainAdmin/CoachEdit')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
