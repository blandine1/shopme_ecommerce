package com.shopme.common.entity.order;

public enum OrderStatus {
	
  NEW  {
		@Override
		public String defaultDescription() {
			return "Oredr was placed by the customer";
		}
	 },	
	
  CANCELLED {
		@Override
		public String defaultDescription() {
			return "Order was rejected";
		}
	},
  
  PROCESSING {
		@Override
		public String defaultDescription() {
			return "Order is being processed";
		}
	},
  
  PACKAGE {
		@Override
		public String defaultDescription() {
			return "Order were package";
		}
	},
	
  PICKED {
		@Override
		public String defaultDescription() {
			return "Shipper picked the package";
		}
	},
  
  SHIPPING {
		@Override
		public String defaultDescription() {
			return "Shipper is delivering the package";
		}
	},
  
  DELIVERED {
		@Override
		public String defaultDescription() {
			return "Order is picked by the costomer";
		}
	},
  
  RETURNED {
		@Override
		public String defaultDescription() {
			return "Customer received the product";
		}
	},
  
  PAID {
		@Override
		public String defaultDescription() {
			return "Customer has paid this order";
		}
	},
  
  REFUNDED {
		@Override
		public String defaultDescription() {
			return "Customer has been refunded";
		}
	};
	
	public abstract String defaultDescription();
}
