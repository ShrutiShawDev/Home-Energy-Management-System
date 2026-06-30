package com.shruti.homeenergy.deviceservice.service;

import com.shruti.homeenergy.deviceservice.dto.DeviceDto;
import com.shruti.homeenergy.deviceservice.entity.Device;
import com.shruti.homeenergy.deviceservice.exception.DeviceNotFoundException;
import com.shruti.homeenergy.deviceservice.repository.DeviceRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDto createDevice(DeviceDto input) {

        Device device = new Device();

        device.setName(input.getName());
        device.setType(input.getType());
        device.setLocation(input.getLocation());
        device.setUserId(input.getUserId());

        final Device savedDevice = deviceRepository.save(device);

        return mapToDto(savedDevice);
    }

    public DeviceDto getDeviceById(Long id){
        Device device = deviceRepository.findById(id)
                .orElseThrow(()-> new DeviceNotFoundException("Device not found"));

        return mapToDto(device);
    }

    public DeviceDto updateDevice(Long id, DeviceDto deviceDto) {

        Device existing = deviceRepository.findById(id)
                .orElseThrow(() ->
                        new DeviceNotFoundException("Device not found with id " + id));

        existing.setName(deviceDto.getName());
        existing.setType(deviceDto.getType());
        existing.setLocation(deviceDto.getLocation());
        existing.setUserId(deviceDto.getUserId());

        final Device updatedDevice = deviceRepository.save(existing);

        return mapToDto(updatedDevice);
    }

    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new DeviceNotFoundException("Device not found with id " + id);
        }

        deviceRepository.deleteById(id);
    }

    public List<DeviceDto> getAllDevicesByUserId(Long userId) {
        List<Device> devices = deviceRepository.findAllByUserId(userId);
        return devices.stream().map(this::mapToDto)
                .toList();
    }

    private DeviceDto mapToDto(Device device){
        DeviceDto deviceDto = DeviceDto.builder()
                .id(device.getId())
                .name(device.getName())
                .location(device.getLocation())
                .userId(device.getUserId())
                .type(device.getType())
                .build();

        return deviceDto;
    }
}
