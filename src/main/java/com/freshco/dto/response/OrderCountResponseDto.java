package com.freshco.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCountResponseDto {

    private long totalOrders;
    private long pendingOrders;
    private long conformedOrders;
    private long processingOrders;
    private long outForDeliveryOrders;
    private long deliveredOrders;
    private long cancelledOrders;

}
