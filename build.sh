#!/bin/bash

# Online Shopping System - Build Script
# This script compiles the JavaFX application

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if JavaFX SDK path is provided
if [ -z "$JAVAFX_SDK" ]; then
    echo -e "${RED}Error: JAVAFX_SDK environment variable not set${NC}"
    echo "Please set JAVAFX_SDK to your JavaFX SDK installation path:"
    echo "export JAVAFX_SDK=/path/to/javafx-sdk-21"
    exit 1
fi

# Check if JavaFX SDK exists
if [ ! -d "$JAVAFX_SDK" ]; then
    echo -e "${RED}Error: JavaFX SDK directory not found at $JAVAFX_SDK${NC}"
    exit 1
fi

echo -e "${YELLOW}=== Online Shopping System Build ===${NC}"
echo "JavaFX SDK: $JAVAFX_SDK"

# Create output directory
mkdir -p out/production/OnlineShoppingSystem

echo -e "${YELLOW}Compiling Java files...${NC}"

# Compile all Java files
javac --module-path "$JAVAFX_SDK/lib" \
      --add-modules javafx.controls,javafx.fxml \
      -d out/production/OnlineShoppingSystem \
      src/module-info.java \
      src/com/shopping/OnlineShoppingApp.java \
      src/com/shopping/datastructures/CustomLinkedList.java \
      src/com/shopping/datastructures/Stack.java \
      src/com/shopping/datastructures/Queue.java \
      src/com/shopping/algorithms/Algorithms.java \
      src/com/shopping/models/Product.java \
      src/com/shopping/models/Customer.java \
      src/com/shopping/models/CartItem.java \
      src/com/shopping/models/Order.java \
      src/com/shopping/models/Complaint.java \
      src/com/shopping/models/UserSession.java \
      src/com/shopping/services/ProductService.java \
      src/com/shopping/services/CartService.java \
      src/com/shopping/services/OrderService.java \
      src/com/shopping/services/CustomerService.java \
      src/com/shopping/services/ComplaintService.java \
      src/com/shopping/controllers/LoginController.java \
      src/com/shopping/controllers/HomeController.java \
      src/com/shopping/controllers/CartController.java \
      src/com/shopping/controllers/CheckoutController.java \
      src/com/shopping/controllers/OrdersController.java \
      src/com/shopping/controllers/AdminController.java

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

# Copy resources
echo -e "${YELLOW}Copying resources...${NC}"
cp -r resources/* out/production/OnlineShoppingSystem/

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Resources copied${NC}"
else
    echo -e "${RED}✗ Failed to copy resources${NC}"
    exit 1
fi

echo -e "${GREEN}=== Build Complete ===${NC}"
echo ""
echo "To run the application, execute:"
echo "java --module-path \"$JAVAFX_SDK/lib\" \\"
echo "     --add-modules javafx.controls,javafx.fxml \\"
echo "     -cp out/production/OnlineShoppingSystem \\"
echo "     com.shopping.OnlineShoppingApp"
