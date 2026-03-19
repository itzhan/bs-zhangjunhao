import request from '@/utils/request'

// 认证
export const login = (data: any) => request.post('/auth/login', data)
export const register = (data: any) => request.post('/auth/register', data)

// 用户
export const getProfile = () => request.get('/users/profile')
export const updateProfile = (data: any) => request.put('/users/profile', data)
export const updatePassword = (data: any) => request.put('/users/password', data)

// 赛事
export const getEvents = () => request.get('/events')
export const getEventDetail = (id: number) => request.get(`/events/${id}`)
export const getCurrentEvent = () => request.get('/events/current')

// 运动项目
export const getSportsByEvent = (eventId: number) => request.get('/sports', { params: { eventId } })
export const getSportDetail = (id: number) => request.get(`/sports/${id}`)

// 报名
export const createRegistration = (data: any) => request.post('/registrations', data)
export const getMyRegistrations = () => request.get('/registrations/my')
export const cancelRegistration = (id: number) => request.put(`/registrations/${id}/cancel`)

// 赛程
export const getMySchedules = () => request.get('/schedules/my')

// 成绩
export const getMyResults = () => request.get('/results/my')
export const getRanking = (params: any) => request.get('/results/ranking', { params })

// 奖项
export const getAwardsByEvent = (eventId: number) => request.get('/awards', { params: { eventId } })
export const getMyAwards = () => request.get('/awards/my')

// 公告
export const getAnnouncements = (params: any) => request.get('/announcements', { params })
export const getAnnouncementDetail = (id: number) => request.get(`/announcements/${id}`)

// 院系
export const getDepartments = () => request.get('/departments')
export const getMedalTable = (eventId?: number) => request.get('/departments/medal-table', { params: { eventId } })
