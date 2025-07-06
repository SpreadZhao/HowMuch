//
//  EveryDayCell.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import IGListKit

final class EveryDayCell: UICollectionViewCell, ListBindable {
    
    private var viewModel: EveryDayCellViewModel?
    
    private lazy var titleLabel = {
        let label = UILabel(frame: .zero)
        return label
    }()
    
    private lazy var descriptionLabel = {
        let label = UILabel(frame: .zero)
        return label
    }()
    
    private lazy var amountLabel = {
        let label = UILabel(frame: .zero)
        return label
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
        setupLayouts()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func bindViewModel(_ viewModel: Any) {
        guard let viewModel = viewModel as? EveryDayCellViewModel else {
            return
        }
        self.viewModel = viewModel
        bind(viewModel: viewModel)
    }
    
    // MARK: - Private
    func bind(viewModel: EveryDayCellViewModel) {
        self.titleLabel.text = viewModel.title
        self.descriptionLabel.text = viewModel.description
        self.amountLabel.text = String(viewModel.amount)
    }
    
    func setupSubviews() {
        contentView.addSubview(titleLabel)
        contentView.addSubview(amountLabel)
        contentView.addSubview(descriptionLabel)
    }
    
    func setupLayouts() {
        titleLabel.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalToSuperview()
            make.width.equalToSuperview().multipliedBy(1.0 / 3.0)
        }
        
        amountLabel.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalTo(titleLabel.snp.right)
            make.width.equalToSuperview().multipliedBy(1.0 / 3.0)
        }
        
        descriptionLabel.snp.makeConstraints { make in
            make.top.bottom.equalToSuperview()
            make.left.equalTo(amountLabel.snp.right)
            make.right.equalToSuperview()
        }
    }
    
}


