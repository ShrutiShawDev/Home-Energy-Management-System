package com.shruti.homeenergy.deviceservice.entity;

import com.shruti.homeenergy.deviceservice.model.DeviceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name="device")
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private DeviceType type;

    private String location;
    private Long userId;
}
