package com.sports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.common.BusinessException;
import com.sports.config.JwtUtil;
import com.sports.dto.LoginDTO;
import com.sports.dto.RegisterDTO;
import com.sports.entity.SysUser;
import com.sports.mapper.DepartmentMapper;
import com.sports.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        result.put("avatar", user.getAvatar());
        return result;
    }

    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setDepartmentId(dto.getDepartmentId());
        user.setClassName(dto.getClassName());
        user.setStudentNo(dto.getStudentNo());
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);
    }

    public SysUser getProfile(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setPassword(null);
        if (user.getDepartmentId() != null) {
            var dept = departmentMapper.selectById(user.getDepartmentId());
            if (dept != null) user.setDepartmentName(dept.getName());
        }
        return user;
    }

    public void updateProfile(Long userId, SysUser updateData) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (updateData.getRealName() != null) user.setRealName(updateData.getRealName());
        if (updateData.getGender() != null) user.setGender(updateData.getGender());
        if (updateData.getPhone() != null) user.setPhone(updateData.getPhone());
        if (updateData.getEmail() != null) user.setEmail(updateData.getEmail());
        if (updateData.getAvatar() != null) user.setAvatar(updateData.getAvatar());
        if (updateData.getClassName() != null) user.setClassName(updateData.getClassName());
        if (updateData.getStudentNo() != null) user.setStudentNo(updateData.getStudentNo());
        if (updateData.getDepartmentId() != null) user.setDepartmentId(updateData.getDepartmentId());
        userMapper.updateById(user);
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    // ========== 管理员方法 ==========

    public Page<SysUser> adminListUsers(int page, int size, String keyword, String role) {
        Page<SysUser> p = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getStudentNo, keyword));
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        Page<SysUser> result = userMapper.selectPage(p, wrapper);
        result.getRecords().forEach(u -> {
            u.setPassword(null);
            if (u.getDepartmentId() != null) {
                var dept = departmentMapper.selectById(u.getDepartmentId());
                if (dept != null) u.setDepartmentName(dept.getName());
            }
        });
        return result;
    }

    public void adminUpdateUser(Long id, SysUser updateData) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (updateData.getRealName() != null) user.setRealName(updateData.getRealName());
        if (updateData.getGender() != null) user.setGender(updateData.getGender());
        if (updateData.getPhone() != null) user.setPhone(updateData.getPhone());
        if (updateData.getEmail() != null) user.setEmail(updateData.getEmail());
        if (updateData.getRole() != null) user.setRole(updateData.getRole());
        if (updateData.getStatus() != null) user.setStatus(updateData.getStatus());
        if (updateData.getDepartmentId() != null) user.setDepartmentId(updateData.getDepartmentId());
        if (updateData.getClassName() != null) user.setClassName(updateData.getClassName());
        if (updateData.getStudentNo() != null) user.setStudentNo(updateData.getStudentNo());
        if (updateData.getPassword() != null && !updateData.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }
        userMapper.updateById(user);
    }

    public void adminDeleteUser(Long id) {
        userMapper.deleteById(id);
    }

    public List<SysUser> getAllUsers() {
        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>().orderByDesc(SysUser::getCreatedAt)
        );
        users.forEach(u -> u.setPassword(null));
        return users;
    }
}
