package kz.hts.ce.service;

import kz.hts.ce.model.entity.WarehouseProduct;
import kz.hts.ce.repository.WarehouseProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseProductService extends BaseService<WarehouseProduct, WarehouseProductRepository> {

    @Autowired
    protected WarehouseProductService(WarehouseProductRepository repository) {
        super(repository);
    }

    public WarehouseProduct findByProductBarcode(long barcode) {
        return repository.findByProduct_Barcode(barcode);
    }

    public List<WarehouseProduct> findByCategoryIdAndShopId(long categoryId, long shopId) {
            return repository.findByProduct_Category_IdAndWarehouse_Shop_Id(categoryId, shopId);
    }
}