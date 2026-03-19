import { request } from '../request';

// ===== 仪表盘 =====
export function fetchDashboard() {
  return request<any>({ url: '/admin/dashboard' });
}
export function fetchMedalTable(eventId?: number) {
  return request<any>({ url: '/admin/statistics/medal-table', params: { eventId } });
}

// ===== 用户管理 =====
export function fetchUserList(params: any) {
  return request<any>({ url: '/admin/users', params });
}
export function updateUser(id: number, data: any) {
  return request<any>({ url: `/admin/users/${id}`, method: 'put', data });
}
export function deleteUser(id: number) {
  return request<any>({ url: `/admin/users/${id}`, method: 'delete' });
}

// ===== 赛事管理 =====
export function fetchEventList(params: any) {
  return request<any>({ url: '/admin/events', params });
}
export function createEvent(data: any) {
  return request<any>({ url: '/admin/events', method: 'post', data });
}
export function updateEvent(id: number, data: any) {
  return request<any>({ url: `/admin/events/${id}`, method: 'put', data });
}
export function deleteEvent(id: number) {
  return request<any>({ url: `/admin/events/${id}`, method: 'delete' });
}

// ===== 运动项目管理 =====
export function fetchSportList(params: any) {
  return request<any>({ url: '/admin/sports', params });
}
export function createSport(data: any) {
  return request<any>({ url: '/admin/sports', method: 'post', data });
}
export function updateSport(id: number, data: any) {
  return request<any>({ url: `/admin/sports/${id}`, method: 'put', data });
}
export function deleteSport(id: number) {
  return request<any>({ url: `/admin/sports/${id}`, method: 'delete' });
}

// ===== 报名管理 =====
export function fetchRegistrationList(params: any) {
  return request<any>({ url: '/admin/registrations', params });
}
export function approveRegistration(id: number) {
  return request<any>({ url: `/admin/registrations/${id}/approve`, method: 'put' });
}
export function rejectRegistration(id: number, reason: string) {
  return request<any>({ url: `/admin/registrations/${id}/reject`, method: 'put', data: { reason } });
}
export function batchApproveRegistrations(ids: number[]) {
  return request<any>({ url: '/admin/registrations/batch-approve', method: 'put', data: { ids } });
}

// ===== 赛程管理 =====
export function fetchScheduleList(params: any) {
  return request<any>({ url: '/admin/schedules', params });
}
export function createSchedule(data: any) {
  return request<any>({ url: '/admin/schedules', method: 'post', data });
}
export function updateSchedule(id: number, data: any) {
  return request<any>({ url: `/admin/schedules/${id}`, method: 'put', data });
}
export function deleteSchedule(id: number) {
  return request<any>({ url: `/admin/schedules/${id}`, method: 'delete' });
}

// ===== 成绩管理 =====
export function fetchResultList(params: any) {
  return request<any>({ url: '/admin/results', params });
}
export function createResult(data: any) {
  return request<any>({ url: '/admin/results', method: 'post', data });
}
export function updateResult(id: number, data: any) {
  return request<any>({ url: `/admin/results/${id}`, method: 'put', data });
}
export function deleteResult(id: number) {
  return request<any>({ url: `/admin/results/${id}`, method: 'delete' });
}

// ===== 奖项管理 =====
export function fetchAwardList(params: any) {
  return request<any>({ url: '/admin/awards', params });
}
export function createAward(data: any) {
  return request<any>({ url: '/admin/awards', method: 'post', data });
}
export function updateAward(id: number, data: any) {
  return request<any>({ url: `/admin/awards/${id}`, method: 'put', data });
}
export function deleteAward(id: number) {
  return request<any>({ url: `/admin/awards/${id}`, method: 'delete' });
}

// ===== 公告管理 =====
export function fetchAnnouncementList(params: any) {
  return request<any>({ url: '/admin/announcements', params });
}
export function createAnnouncement(data: any) {
  return request<any>({ url: '/admin/announcements', method: 'post', data });
}
export function updateAnnouncement(id: number, data: any) {
  return request<any>({ url: `/admin/announcements/${id}`, method: 'put', data });
}
export function deleteAnnouncement(id: number) {
  return request<any>({ url: `/admin/announcements/${id}`, method: 'delete' });
}

// ===== 院系管理 =====
export function fetchDepartmentList() {
  return request<any>({ url: '/admin/departments' });
}
export function createDepartment(data: any) {
  return request<any>({ url: '/admin/departments', method: 'post', data });
}
export function updateDepartment(id: number, data: any) {
  return request<any>({ url: `/admin/departments/${id}`, method: 'put', data });
}
export function deleteDepartment(id: number) {
  return request<any>({ url: `/admin/departments/${id}`, method: 'delete' });
}

// ===== 系统配置 =====
export function fetchSettings() {
  return request<any>({ url: '/admin/settings' });
}
export function updateSettings(data: any[]) {
  return request<any>({ url: '/admin/settings', method: 'put', data });
}
