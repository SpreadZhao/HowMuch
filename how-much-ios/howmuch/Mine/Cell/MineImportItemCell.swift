//
//  MineImportItemCell.swift
//  howmuch
//
//  Created by ljx on 2025/6/29.
//

import IGListKit
import Combine

final class MineImportItemCell: UICollectionViewCell {
    
    private var viewModel: MineImportItemCellViewModel?
    private var cancellables = Set<AnyCancellable>()
    
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
    
    override func prepareForReuse() {
        super.prepareForReuse()
        cancellables.removeAll()
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

extension MineImportItemCell: ListBindable {
    
    func bindViewModel(_ viewModel: Any) {
        guard let viewModel = viewModel as? MineImportItemCellViewModel else {
            return
        }
        self.viewModel = viewModel
        bind(viewModel: viewModel)
    }
    
    func bind(viewModel: MineImportItemCellViewModel) {
        viewModel.$title.receive(on: RunLoop.main)
            .sink(receiveValue: { [weak self] in
                self?.itemLabel.text = $0
            })
            .store(in: &cancellables)
        
        viewModel.$iconName.receive(on: RunLoop.main)
            .sink(receiveValue: { [weak self] in
                self?.iconImageView.image = UIImage(systemName: $0)
            })
            .store(in: &cancellables)
    }
    
}
