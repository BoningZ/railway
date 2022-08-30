import axios from "axios";
import { store } from "@/store/createStore.js"

function generalRequest(url, data) {
    return axios.post(
        url,
        {
            data: data
        },
        {
            headers: {
                Authorization: 'Bearer ' + store.state.jwtToken
            }
        }
    ).then(res => {
        return res.data.data
    })
}

function samplePost() {
    return generalRequest('/api/teach/admin', null)
}

function getMenuList() {
    return generalRequest('/api/teach/getMenuList', null)
}

function getCourseList(data) {
    return generalRequest('/api/teach/getCourseList', data)
}
function changePassword(data) {
    return generalRequest('/api/teach/changePassword', data)
}
function register(data){
    return generalRequest('/api/auth/signup',data)
}
function courseDelete(data) {
    return generalRequest('/api/teach/courseDelete', data)
}
function getCourseInfo(data) {
    return generalRequest('/api/teach/getCourseInfo', data)
}
function courseSubmit(data) {
    return generalRequest('/api/teach/courseSubmit', data)
}
function getProfile(data){
    return generalRequest('/api/teach/getProfile',data);
}
function submitProfile(data){
    return generalRequest('/api/teach/submitProfile',data);
}
function getSelectedList(data){
    return generalRequest('api/select/getSelected',data);
}
function unselect(data){
    return generalRequest('api/select/unselect',data);
}
function select(data){
    return generalRequest('api/select/select',data);
}
function getWeeklyTable(data){
    return generalRequest('api/select/getWeekly',data);
}
function getStudentList(data){
    return generalRequest('api/score/getStudents',data);
}
function getScoreList(data){
    return generalRequest('api/score/getScores',data);
}
function getGPA(data){
    return generalRequest('api/score/getGPA',data);
}
function submitScore(data){
    return generalRequest('api/score/submitScore',data);
}
function getCountries(data){
    return generalRequest('api/info/country',data);
}
function getCompanies(data){
    return generalRequest('api/info/company',data);
}
function getStations(data){
    return generalRequest('/api/info/station',data);
}
function getStationsLike(data){
    return generalRequest('/api/info/stationLike',data);
}
function getCityWithPos(data){
    return generalRequest('/api/info/cityWithPos',data);
}
function getStationWithPos(data){
    return generalRequest('/api/info/stationWithPos',data);
}
function queryRoute(data){
    return generalRequest('/api/ticket/getTransferInfo',data)
}

//  company


export {
    getScoreList,
    getGPA,
    getStudentList,
    submitScore,
    samplePost,
    getMenuList,
    getCourseList,
    changePassword,
    register,
    courseDelete,
    getCourseInfo,
    courseSubmit,
    getProfile,
    submitProfile,
    getSelectedList,
    select,
    unselect,
    getWeeklyTable,
    getCountries,
    getCompanies,
    getStations,
    getStationsLike,
    getStationWithPos,
    getCityWithPos,
    queryRoute
}
