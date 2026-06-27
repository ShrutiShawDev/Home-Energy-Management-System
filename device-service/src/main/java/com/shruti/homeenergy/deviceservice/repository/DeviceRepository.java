package com.shruti.homeenergy.deviceservice.repository;

import com.shruti.homeenergy.deviceservice.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
