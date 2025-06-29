//
//  MineSettingItemCell.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import UIKit
import SnapKit
import IGListKit

final class MineSettingItemCell: UICollectionViewCell {
    
    private var title: String?
    private var imageName: String?
    
    private var itemLabel: UILabel = {
        return UILabel()
    }()
    
    private var iconImageView: UIImageView = {
        return UIImageView(frame: .zero)
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
        setupLayouts()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Private
    func setupSubviews() {
        addSubview(itemLabel)
        addSubview(iconImageView)
    }
    
    func setupLayouts() {
        itemLabel.snp.makeConstraints { make in
            make.left.equalTo(self.snp.left)
            make.top.equalTo(self.snp.top)
            make.bottom.equalTo(self.snp.bottom)
        }
        iconImageView.snp.makeConstraints { make in
            make.right.equalTo(self.snp.right)
            make.width.equalTo(self.snp.height)
            make.height.equalTo(self.snp.height)
            make.centerY.equalTo(self.snp.centerY)
        }
    }
    
}

extension MineSettingItemCell: ListBindable {
    // MARK: - ListBindable
    func bindViewModel(_ viewModel: Any) {
        guard let viewModel = viewModel as? MineSettingItemCellViewModel else {
            return
        }
        self.title = viewModel.title
        self.imageName = viewModel.iconName
        self.itemLabel.text = self.title
        if let imageName = self.imageName {
            iconImageView.image = UIImage(systemName: imageName)
        }
    }
}
