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
  
  PACKAGED {
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
			return "Customer received  products";
		}
	},
  
  RETURN_REQUESTED {
		@Override
		public String defaultDescription() {
			return "Customer send request to return purchase";
		}
	},
  
  RETURNED {
		@Override
		public String defaultDescription() {
			return "Customer refused the product";
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
