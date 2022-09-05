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
function getTrainTypes(data){
    return generalRequest('/api/info/trainType',data)
}
function getCoaches(data){
    return generalRequest('/api/info/coach',data)
}
function getTrainsLike(data){
    return generalRequest('/api/info/trainLike',data)
}
function getDrivers(data){
    return generalRequest('/api/info/driver',data)
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
function getTravel(data){
    return generalRequest('/api/ticket/getTravel',data)
}
function getTravelInfo(data){
    return generalRequest('/api/ticket/getTravelInfo',data)
}
function payTravel(data){
    return generalRequest('/api/ticket/payTravel',data)
}
function getFellowList(data){
    return generalRequest('/api/fellow/getFellow',data)
}
function addFellow(data){
    return generalRequest('/api/fellow/addFellow',data)
}
function getTravelList(data){
    return generalRequest('/api/ticket/getTravelList',data)
}
function alterTicket(data){
    return generalRequest('/api/ticket/alterTicket',data)
}
function getAlterRoutes(data){
    return generalRequest('/api/ticket/getAlterRoutes',data)
}
function getRefundInfo(data){
    return generalRequest('/api/ticket/getRefundInfo',data);
}
function refund(data){
    return generalRequest('/api/ticket/refund',data);
}
function getSeatTable(data){
    return generalRequest('/api/seat/getSeatTable',data);
}
function submitSeat(data){
    return generalRequest('/api/seat/submitSeat',data)
}
function newSeat(data){
    return generalRequest('/api/seat/newSeat',data)
}
function getCoachTable(data){
    return generalRequest('/api/coach/getCoachTable',data);
}
function getCoachInfo(data){
    return generalRequest('/api/coach/getCoachInfo',data)
}
function submitCoach(data){
    return generalRequest('/api/coach/submitCoach',data)
}
function getTrainTable(data){
    return generalRequest('/api/train/getTrainTable',data)
}
function getTrainInfo(data){
    return generalRequest('/api/train/getTrainInfo',data)
}
function submitTrain(data){
    return generalRequest('/api/train/submitTrain',data)
}
function getLineInfo(data){
    return generalRequest('/api/line/getLineInfo',data)
}
function getLineTable(data){
    return generalRequest('/api/line/getLineTable',data)
}
function submitLineStopping(data){
    return generalRequest('/api/line/submitLineStopping',data)
}
function submitLineDeparture(data){
    return generalRequest('/api/line/submitLineDeparture',data)
}
function submitLinePrice(data){
    return generalRequest('/api/line/submitLinePrice',data)
}
function getSalesStat(data){
    return generalRequest('/api/sales/stat',data)
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
    queryRoute,
    getTravel,
    getTravelInfo,
    payTravel,
    getFellowList,
    addFellow,
    getTravelList,
    alterTicket,
    getAlterRoutes,
    getRefundInfo,
    refund,
    getSeatTable,
    submitSeat,
    newSeat,
    getCoachTable,
    getCoachInfo,
    submitCoach,
    getTrainInfo,
    getTrainTable,
    submitTrain,
    getCoaches,
    getTrainTypes,
    getLineInfo,
    getLineTable,
    submitLineStopping,
    submitLinePrice,
    submitLineDeparture,
    getTrainsLike,
    getDrivers,
    getSalesStat
}
